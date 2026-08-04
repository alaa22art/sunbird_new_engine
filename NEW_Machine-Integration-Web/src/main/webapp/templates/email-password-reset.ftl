<@layout.extends name="email-base.ftl">
    <@layout.put block="contents">
            <td>
				<p class="dense-p">Dear ${name},</p>
                <p>Click on the following link to reset your password: <a href="${resetPasswordLink}">Reset</a></p>
            </td>
    </@layout.put>
</@layout.extends>