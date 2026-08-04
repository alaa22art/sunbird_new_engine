<!DOCTYPE html PUBLIC "-//W3C//Dth XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/Dth/xhtml1-transitional.dth">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
    <title>Email</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <style>
        body {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        

        table {
            border-collapse: collapse;
            width: 500px;
        }

        .main-color {
            background-color: rgb(36, 112, 156);
        }
        
        .logo {
            float: left;
            padding-top: 10px;
            padding-bottom: 10px;
            height: 100px;
            display: block;
            margin-left: 25px;

        }

        .content-row {
            background-color: rgba(245, 245, 245, 1);
        }

        p {
            margin-left: 25px;
            margin-right: 25px;
        }

        .dense-p {
            margin-top: 8px;
            margin-bottom: 8px;
        }

        .credentials {
            font-style: italic;
        }

        .contact-us{
            color: #e3ecf2 !important;
        }
        .contact-us a{
            color: #e3ecf2 !important;
        } 
        
        p.ex1 {
  font-size: 30px;
}
p.ex2 {
  font-size: 50px;
}

    </style>
</head> 
<body>
    <table align="center" border="2" cellpadding="0" cellspacing="0">
        <tr>
            <th align="left" colspan="2">
            
            <p class="ex1">Certacure Engine</p>	
             <p>Auto Notification </p>	
                          
           </th>
         
           
            <th align="right" colspan="1">
            
              <img src="https://certacure.com/wp-content/uploads/2017/07/logo-certa_00000.png" class="logo row-margin" title="Certacure Logo" alt="Certacure Logo" style="display: block;" width="100" height="75"  />
            
            </th>
            
            
        </tr>
        
        <tr>
       
        <@layout.block name="contents" colspan="3">
            </@layout.block>
            
        </tr>
        <tr class="main-color contact-us">
            <th colspan="3">
                <p class="dense-p">Thanks</p>
                <p class="dense-p">&copy; 2024
                    <a href="https://certacure.com/">Certacure</a>, All rights reserved.</p>
            </th>
              
        </tr>
    </table>
</body>
</html>