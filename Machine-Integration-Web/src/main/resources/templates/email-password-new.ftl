<@layout.extends name="email-base.ftl">
    <@layout.put block="contents"> 
            <td>
				<p class="dense-p">Dear ${name},</p>
                <p>Your Password has been changed to: <b>${password}</b></p>
            </td>
    </@layout.put>
</@layout.extends>