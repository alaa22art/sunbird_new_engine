<@layout.extends name="email-base.ftl">
    <@layout.put block="contents"> 
            <th colspan="3">
				<p class="dense-p">Dear KHCC ,</p>
                <p>Welcome to Certacure </p>
				<p>This is To Notify you that there is a New Error Raised From Certacure Engine , With The Follwing Details :</p>
				<p>Patient ID : <a> ${patientId} </a></p>
				<p>Admission No : <a> ${admissionNo} </a></p>
				<p>Patient ID : <a> ${patientId} </a></p>
				<p>Event Date Time : <a> ${eventDateTime} </a></p>
				<p>Operation Type : <a> ${adtOperationType} </a></p>
				<p>Message Control ID : <a> ${msgControlId} </a></p>
				<p>Message Error Details : <a> ${msgDetails} </a></p>
				<p>Message Text : <a> ${messageBody}</a></p>							            
            </th>
    </@layout.put>
</@layout.extends>