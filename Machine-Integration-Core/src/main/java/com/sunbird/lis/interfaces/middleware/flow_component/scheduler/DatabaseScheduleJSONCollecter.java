package com.sunbird.lis.interfaces.middleware.flow_component.scheduler;

import static com.sunbird.lis.interfaces.middleware.flow_component.astme138191async.AstmE138191AsyncProtocol.ENQBytes;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.sunbird.core.common.util.SpringUtil;
import com.sunbird.lis.interfaces.entities.DataInboundJSONMessage;
import com.sunbird.lis.interfaces.entities.DataInboundTempTable;
import com.sunbird.lis.interfaces.entities.Machine;
import com.sunbird.lis.interfaces.middleware.core.FlowComponent;
import com.sunbird.lis.interfaces.middleware.core.RecipientConf;
import com.sunbird.lis.interfaces.middleware.flow_component.lab_http.httpRequstTransaction;
import com.sunbird.lis.interfaces.middleware.interfaces.LabMessages.LabOrderMsg;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.msg.LIS2A2OrderMsg;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.CommentRecord;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.OrderRecord;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.QueryRecord;
import com.sunbird.lis.interfaces.middleware.interfaces.lis2a2.record.TerminationRecord;
import com.sunbird.lis.interfaces.service.DataInboundHL7MessageService;
import com.sunbird.lis.interfaces.service.DataInboundJSONMessageService;
import com.sunbird.lis.interfaces.service.DataInboundTempTableService;
import com.sunbird.lis.interfaces.service.MachineService;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.japi.pf.ReceiveBuilder;
import scala.PartialFunction;
import scala.concurrent.duration.FiniteDuration;
import scala.runtime.BoxedUnit;

public class DatabaseScheduleJSONCollecter extends FlowComponent<RecipientConf> {

    private Config config= null;

    private MachineService machineService;
    private Machine machine;
    private DataInboundTempTable inboundJSONMessage;
    private DataInboundTempTableService JSONMessageService;

    @Override
    protected void init() {
        System.out.println("DB COLLECTER BUILD");

        context().system().scheduler().schedule(
            new FiniteDuration(10, TimeUnit.SECONDS), new FiniteDuration(10, TimeUnit.SECONDS),
            self(),
            new DBFetchManager(), context().dispatcher(), self());
    }

    @Override
    protected PartialFunction<Object, BoxedUnit> getBehaviour() {
        return ReceiveBuilder
            .match(String.class, this::convertAndForward)

            .match(DBFetchManager.class, __ -> {
                System.out.println("DB COLLECTER CALLED AGINE");
                // throw new RuntimeException("Restart needed");

                List<DataInboundTempTable> lstAllJSONMessages = selectJsonMessage();
                
                if(lstAllJSONMessages.size() > 0 )
                {
                    
                    for(int i = 0 ; i< lstAllJSONMessages.size() ; i++)
                    {
                        conf.recipient.tell(lstAllJSONMessages.get(i), self());
                    }
                  
                }

            })

            .build();
    }
    
    
    private List<DataInboundTempTable > selectJsonMessage() {
        
        List<DataInboundTempTable> dataInboundList = new ArrayList<DataInboundTempTable>();
        String url = "jdbc:postgresql://localhost:5433/certacure_middleware2";
        String user = "postgres";
        String password = "root";

         String SELECT_PENDING_MESSAGES_SQL = "SELECT * FROM  mw_qms_temp_table" +
          "  WHERE is_sent = 0 ";
         
         
        // System.out.println(SELECT_PENDING_MESSAGES_SQL);
          // Step 1: Establishing a Connection
          try (Connection connection = DriverManager.getConnection(url, user, password);

              // Step 2:Create a statement using connection object
              PreparedStatement preparedStatement = 
                  connection.prepareStatement(SELECT_PENDING_MESSAGES_SQL);) {
              System.out.println(preparedStatement);
              // Step 3: Execute the query or update query
              ResultSet rs = preparedStatement.executeQuery();

              // Step 4: Process the ResultSet object.
              while (rs.next()) {
                  int id = rs.getInt("rid");
                  String message_body = rs.getString("message_body");
                  String message_control_id = rs.getString("message_control_id");
                  boolean is_sent = rs.getInt("is_sent") == 0 ? false : true;
                  boolean is_succuss = rs.getInt("is_succuss") == 0 ? false : true;
                  System.out.println(id + "," + message_body + "," + message_control_id + "," + is_succuss +    
                      "," + is_sent);
                  
                  DataInboundTempTable dataItem = new DataInboundTempTable(id ,  message_body,  
                      message_control_id ,is_sent  , is_succuss);
                  
                  dataInboundList.add(dataItem);
                  
              }
              
              
              
          } catch (SQLException e) {

              // print SQL exception information
              printSQLException(e);
              
              return null;
          }
          
          return dataInboundList;
          
         
        
        
        

  }
    
    
    public static void printSQLException(SQLException ex) {
        for (Throwable e: ex) {
            if (e instanceof SQLException) {
                e.printStackTrace(System.err);
                System.err.println("SQLState: " + ((SQLException) e).getSQLState());
                System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
                System.err.println("Message: " + e.getMessage());
                Throwable t = ex.getCause();
                while (t != null) {
                    System.out.println("Cause: " + t);
                    t = t.getCause();
                }
            }
        }
    }
            
        

  
  

    

    private void convertAndForward(httpRequstTransaction httpRequstTransObject) {
        conf.recipient.tell(httpRequstTransObject, self());
    }

    private void convertAndForwardOrderMsg(LabOrderMsg[] arrLabOrderMsg) {

    }

    private void convertAndForward(String strData) {

    }

    private Machine getMachine() {
        machineService= (MachineService) SpringUtil.getBean("MachineService");
        machine= machineService.getMachineByActorPath(getContext().parent().toString());
        return machine;
    }

    private class DBFetchManager implements Serializable {
        public DBFetchManager() {
            System.out.println("DB FETCH MANAGER CALLED");
        }

    }

}
