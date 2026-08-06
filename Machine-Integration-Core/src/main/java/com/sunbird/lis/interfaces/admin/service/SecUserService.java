package com.sunbird.lis.interfaces.admin.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.sunbird.core.base.helper.SearchCriterion;
import com.sunbird.core.base.helper.SearchCriterion.FilterOperator;
import com.sunbird.core.base.service.GenericService;
import com.sunbird.core.common.business.exception.BusinessException;
import com.sunbird.core.common.business.exception.BusinessException.ErrorSeverity;
import com.sunbird.core.common.helper.Email;
import com.sunbird.core.common.helper.FilterablePageRequest;
import com.sunbird.core.common.helper.TokenBuilder;
import com.sunbird.core.common.util.CollectionUtil;
import com.sunbird.core.common.util.EmailUtil;
import com.sunbird.core.common.util.SecurityUtil;
import com.sunbird.core.common.util.StringUtil;
import com.sunbird.core.common.util.TokenUtil;
import com.sunbird.lis.interfaces.admin.model.SecGroupRole;
import com.sunbird.lis.interfaces.admin.model.SecGroupUser;
import com.sunbird.lis.interfaces.admin.model.SecUser;
import com.sunbird.lis.interfaces.admin.model.SecUserRole;
import com.sunbird.lis.interfaces.admin.repo.SecUserRepo;
import com.sunbird.lis.interfaces.annotation.InterceptorFree;
import com.sunbird.lis.interfaces.helper.MachineIntegrationRights;

import freemarker.template.TemplateException;

/**
 * SecUserService.java
 * 
 **/
@Service("SecUserService")
public class SecUserService extends GenericService<SecUser, SecUserRepo> {

	@Autowired
	private SecUserRepo secUserRepo;
	@Autowired
	private SecGroupUserService secGroupUserService;
	@Autowired
	private SecUserRoleService secUserRoleService;
	@Value("${system.website.url}")
	private String serverUrl;

	public static JavaMailSenderImpl generateMailSender() {
		JavaMailSenderImpl javaMailSenderImpl = new JavaMailSenderImpl();
		javaMailSenderImpl.setUsername(EmailUtil.DEFAULT_MAIL_SENDER.getUsername());
		javaMailSenderImpl.setPassword(EmailUtil.DEFAULT_MAIL_SENDER.getPassword());
		javaMailSenderImpl.setHost(EmailUtil.DEFAULT_MAIL_SENDER.getHost());
		javaMailSenderImpl.setPort(EmailUtil.DEFAULT_MAIL_SENDER.getPort());
		//set some default properties
		javaMailSenderImpl.setDefaultEncoding(EmailUtil.DEFAULT_MAIL_SENDER.getDefaultEncoding());
		javaMailSenderImpl.setJavaMailProperties(EmailUtil.DEFAULT_MAIL_SENDER.getJavaMailProperties());
		javaMailSenderImpl.setProtocol(EmailUtil.DEFAULT_MAIL_SENDER.getProtocol());
		return javaMailSenderImpl;
	}

	/**
	 * For Admin when creating a user.
	 * 
	 * @param secUser
	 * @param secGroupUserList
	 * @param secUserRoleList
	 * @return
	 * @throws MailException
	 * @throws InterruptedException
	 * @throws MessagingException
	 * @throws TemplateException
	 * @throws IOException
	 */
	@PreAuthorize("hasAuthority('" + MachineIntegrationRights.ADD_USER + "')")
	public SecUser createUser(SecUser secUser, List<SecGroupUser> secGroupUserList, List<SecUserRole> secUserRoleList) {
		//we are checking if username exists from outside the function because we need the intercepter work inside this function
		if (findUserByEmail(secUser.getEmail()) != null) {
			throw new BusinessException("Email is used", "emailExist", ErrorSeverity.ERROR);
		}

		String generatedPassword = SecurityUtil.generatePassword();
		String encryptedPassword = SecurityUtil.encode(generatedPassword);
		secUser.setPassword(encryptedPassword);
		secUser.setEmail(secUser.getEmail().toLowerCase());
		SecUser newSecUser = getRepository().save(secUser);

		if (!CollectionUtil.isCollectionEmpty(secGroupUserList)) {
			secGroupUserList.stream().forEach(sgu -> sgu.setSecUser(newSecUser));
			secGroupUserService.createGroupUser(secGroupUserList);
		}
		if (!CollectionUtil.isCollectionEmpty(secUserRoleList)) {
			secUserRoleList.stream().forEach(sur -> sur.setSecUser(newSecUser));
			secUserRoleService.createUserRole(secUserRoleList);
		}

		Map<String, String> templateValues = new HashMap<>();
		templateValues.put("username", secUser.getUsername());
		templateValues.put("password", generatedPassword);
		templateValues.put("loginUrl", serverUrl + "/login");
		templateValues.put("userProfileUrl", serverUrl + "/user-profile");
		templateValues.put("name", secUser.getFirstName().entrySet().iterator().next().getValue());
		Email email = new Email(secUser.getEmail(), "New User", "");
		email.setTemplateUri("email-new-user");
		email.setTemplateValueMap(templateValues);
		email.setSender(generateMailSender());
		EmailUtil.sendMailTemplate(email);
		return newSecUser;

	}

	@PreAuthorize("hasAuthority('" + MachineIntegrationRights.DEACTIVATE_USER + "')")
	public void deactivateUser(SecUser secUser) {
		secUser.setIsActive(Boolean.FALSE);
		secUser.setPassword(getPasswordById(secUser.getRid()));
		getRepository().save(secUser);
	}

	@PreAuthorize("hasAuthority('" + MachineIntegrationRights.ACTIVATE_USER + "')")
	public void activateUser(SecUser secUser) {
		secUser.setIsActive(Boolean.TRUE);
		secUser.setPassword(getPasswordById(secUser.getRid()));
		getRepository().save(secUser);
	}

	/**
	 * For Admin when editing the user.
	 * 
	 * @param secUser
	 * @param secGroupUserList
	 * @param secUserRoleList
	 * @return
	 */
	@PreAuthorize("hasAuthority('" + MachineIntegrationRights.UPD_USER + "')")
	public SecUser updateUser(SecUser secUser, List<SecGroupUser> secGroupUserList,
			List<SecUserRole> secUserRoleList) {
		SecUser user = findUserByEmail(secUser.getEmail());
		if (user != null && !user.equals(secUser)) {
			throw new BusinessException("Email is used", "emailExist", ErrorSeverity.ERROR);
		}
		secUser.setPassword(getPasswordById(secUser.getRid()));
		SecUser updatedSecUser = getRepository().save(secUser);

		secGroupUserService.deleteGroupUserByUser(updatedSecUser);
		if (!CollectionUtil.isCollectionEmpty(secGroupUserList)) {
			secGroupUserList.stream().forEach(sgu -> sgu.setSecUser(updatedSecUser));
			secGroupUserService.createGroupUser(secGroupUserList);
		}

		secUserRoleService.deleteUserRoleByUser(updatedSecUser);
		if (!CollectionUtil.isCollectionEmpty(secUserRoleList)) {
			secUserRoleList.stream().forEach(sur -> sur.setSecUser(updatedSecUser));
			secUserRoleService.createUserRole(secUserRoleList);
		}

		return updatedSecUser;
	}

	/**
	 * For users when updating their profiles.
	 * 
	 * @param secUser
	 * @param token : getting the authorities from the token
	 * @return
	 */
	public void updateUserProfile(SecUser secUser) {
		secUser.setPassword(getPasswordById(secUser.getRid()));
		getRepository().save(secUser);
	}

	/**
	 * For Admin , to reset the secUser password
	 * 
	 * @param secUser
	 * @return
	 * @throws MailException
	 * @throws InterruptedException
	 * @throws MessagingException
	 * @throws TemplateException
	 * @throws IOException
	 */
	@PreAuthorize("hasAuthority('" + MachineIntegrationRights.RESET_PASS_USER + "')")
	public String resetUserPassword(SecUser secUser) {

		String generatedPassword = SecurityUtil.generatePassword();
		String encryptedPassword = SecurityUtil.encode(generatedPassword);
		secUser.setPassword(encryptedPassword);
		getRepository().save(secUser);

		Map<String, String> templateValues = new HashMap<>();
		templateValues.put("password", generatedPassword);
		templateValues.put("name", secUser.getFirstName().entrySet().iterator().next().getValue());
		Email email = new Email(secUser.getEmail(), "New Password", "");
		email.setTemplateUri("email-password-new");
		email.setTemplateValueMap(templateValues);
		email.setSender(generateMailSender());
		EmailUtil.sendMailTemplate(email);

		return generatedPassword;

	}

	/**
	 * Send an email to the user containing a token to change the password.
	 * 
	 * @param username
	 * 
	 * 
	 * @throws MailException
	 * @throws InterruptedException
	 * @throws MessagingException
	 * @throws TemplateException
	 * @throws IOException
	 */
	@InterceptorFree
	public void forgotPassword(String username) {
		SecUser lostUser = findUserByUsername(username);
		// we don't notify the user that the user name exists or not
		if (lostUser == null) {
			return;
		}

		TokenBuilder tokenBuilder = new TokenBuilder();
		tokenBuilder
					.setVersion(TokenUtil.SYSTEM_VERSION)
					.setUser(lostUser)
					.setExpiration(TokenUtil.FORGOT_TOKEN_EXPIRATION);
		String token = tokenBuilder.build();
		String url = serverUrl + "/password-reset?t=" + token;
		Map<String, String> templateValues = new HashMap<>();
		templateValues.put("resetPasswordLink", url);
		templateValues.put("name", lostUser.getFirstName().entrySet().iterator().next().getValue());
		Email springEmail = new Email(lostUser.getEmail(), "Password Reset", "");
		springEmail.setTemplateUri("email-password-reset");
		springEmail.setTemplateValueMap(templateValues);
		springEmail.setSender(generateMailSender());
		EmailUtil.sendMailTemplate(springEmail);
	}

	@InterceptorFree
	public void updatePassword(Long userRid, String encodedPassword) {
		getRepository().updatePassword(userRid, encodedPassword);
	}

	/**
	 * After verification of the token this method is called to apply the new password
	 * 
	 * @param token
	 * @param newPassword
	 */
	@SuppressWarnings("unchecked")
	@InterceptorFree
	public void changeForgottenPassword(String token, String newPassword) {
		Long userRid = TokenUtil.getObjectFromToken(token, "user", SecUser.class).getRid();
		String encryptedPassword = SecurityUtil.encode(newPassword);
		updatePassword(userRid, encryptedPassword);
	}

	/**
	 * Change user's password by the user.
	 * 
	 * @param secUser
	 * @param token : getting the authorities from the token
	 * @param originalPassword
	 * @param newPassword
	 * @param newEmail
	 * @return
	 * @throws MailException
	 * @throws InterruptedException
	 * @throws MessagingException
	 * @throws TemplateException
	 * @throws IOException
	 */
	@PreAuthorize("hasAuthority('" + MachineIntegrationRights.CHANGE_PASS_EMAIL_USER + "')")
	public void updateUserEmailAndPass(String originalPassword, String newPassword,
			String newEmail) {
		SecUser user = getRepository().findOne(
				Arrays.asList(new SearchCriterion("rid", SecurityUtil.getCurrentUser().getRid(), FilterOperator.eq)), SecUser.class,
				"lkpGender", "lkpUserStatus", "comLanguage");
		SecUser duplicatedUser = findUserByEmail(newEmail);
		if (duplicatedUser != null && !duplicatedUser.equals(user)) {
			throw new BusinessException("Email is used", "emailExist", ErrorSeverity.ERROR);
		}

		if (SecurityUtil.isPasswordsMatch(originalPassword, getPasswordById(user.getRid()))) {
			user.setEmail(newEmail);
			String password = !StringUtil.isEmpty(newPassword) ? newPassword : originalPassword;
			String encryptedPassword = SecurityUtil.encode(password);
			user.setPassword(encryptedPassword);
			user = getRepository().save(user);
			Map<String, String> templateValues = new HashMap<>();
			templateValues.put("password", password);
			templateValues.put("name", user.getFirstName().entrySet().iterator().next().getValue());

			Email email = new Email(newEmail, "New Password", "");
			email.setTemplateUri("email-password-new");
			email.setTemplateValueMap(templateValues);
			email.setSender(generateMailSender());
			EmailUtil.sendMailTemplate(email);
		} else {
			throw new BusinessException("Password Does Not Match", "passwordInvalid", ErrorSeverity.ERROR);
		}

	}

	/**
	 * Get a page of users without joining roles or groups
	 * 
	 * @param filterablePageRequest
	 * @return page of users
	 */
	@PreAuthorize("hasAuthority('" + MachineIntegrationRights.VIEW_USERS_MANAGEMENT + "')")
	public Page<SecUser> findUserPage(FilterablePageRequest filterablePageRequest) {

		String[] joins = new String[] { "comLanguage", "lkpGender", "lkpUserStatus" };

		Page<SecUser> page = getRepository().find(filterablePageRequest.getFilters(), filterablePageRequest.getPageRequest(),
				SecUser.class, joins);

		return page;
	}

	/**
	 * Get a page of users without joining roles, used in Groups Management
	 * 
	 * @param filterablePageRequest
	 * @return page of users
	 */

	public Page<SecUser> findUserPageJoinGroups(FilterablePageRequest filterablePageRequest) {

		Page<SecUser> page = getRepository().find(filterablePageRequest.getFilters(), filterablePageRequest.getPageRequest(),
				SecUser.class);
		if (page.getNumberOfElements() == 0) {
			return page;
		}
		List<SecUser> users = getRepository()	.find(
				Arrays.asList(new SearchCriterion("rid", page.getContent().stream().map(SecUser::getRid).collect(Collectors.toList()),
						FilterOperator.in)),
				SecUser.class, filterablePageRequest.getSortObject(), "secGroupUsers.secGroup")
												.stream().distinct().collect(Collectors.toList());
		for (SecUser su : users) {
			for (SecGroupUser sgu : su.getSecGroupUsers()) {
				su.getUserGroups().add(sgu.getSecGroup());
			}
		}
		Page<SecUser> usersPage = new PageImpl<>(users, filterablePageRequest.getPageRequest(), page.getTotalElements());

		return usersPage;
	}

	/**
	 * Get a page of users without joining groups, used in Roles Management
	 * 
	 * @param filterablePageRequest
	 * @return page of users
	 */

	public Page<SecUser> findUserPageJoinRolesGroups(FilterablePageRequest filterablePageRequest) {

		Page<SecUser> page = getRepository().find(filterablePageRequest.getFilters(), filterablePageRequest.getPageRequest(),
				SecUser.class);
		if (page.getNumberOfElements() == 0) {
			return page;
		}
		List<SecUser> users = getRepository()	.find(
				Arrays.asList(new SearchCriterion("rid", page.getContent().stream().map(SecUser::getRid).collect(Collectors.toList()),
						FilterOperator.in)),
				SecUser.class, filterablePageRequest.getSortObject(), "secUserRoles.secRole",
				"secGroupUsers.secGroup.secGroupRoles.secRole")
												.stream().distinct().collect(Collectors.toList());
		for (SecUser su : users) {
			for (SecUserRole sur : su.getSecUserRoles()) {
				su.getUserRoles().add(sur.getSecRole());
			}
			for (SecGroupUser sgu : su.getSecGroupUsers()) {
				for (SecGroupRole sgr : sgu.getSecGroup().getSecGroupRoles()) {
					su.getUserRoles().add(sgr.getSecRole());
				}
			}
		}
		Page<SecUser> usersPage = new PageImpl<>(users, filterablePageRequest.getPageRequest(), page.getTotalElements());
		return usersPage;
	}

	public SecUser findUserByEmail(String email) {
		return getRepository().findByEmailIgnoreCase(email);
	}

	@InterceptorFree
	public SecUser findUserByUsername(String username) {
		return getRepository().findByUsernameIgnoreCase(username);
	}

	/*
	 * To set the password for the SecUser from the Database, because of the JSON ignore
	 */
	public String getPasswordById(Long Id) {
		return getRepository().fetchPasswordById(Id);
	}

	public void updateLastLoginTime(Long userRid) {
		getRepository().updateLastLoginTime(userRid, new Date());
	}

	public void deleteUser(Long rid) {
		getRepository().deleteById(rid);
	}

	public String getUserLocale(Long userRid) {
		return getRepository().findOne(Arrays.asList(new SearchCriterion("rid", userRid, FilterOperator.eq)),
				SecUser.class, "comLanguage").getComLanguage().getLocale();
	}

	@Override
	protected SecUserRepo getRepository() {
		return secUserRepo;
	}

}
