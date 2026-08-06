package com.sunbird.core.common.util;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.jasypt.util.text.BasicTextEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Component;

import com.sunbird.core.base.entity.UserAccount;
import com.sunbird.core.base.entity.UserAccountImpl;
import com.sunbird.core.common.business.exception.BusinessException;
import com.sunbird.core.common.business.exception.BusinessException.ErrorSeverity;
import com.sunbird.core.common.helper.PasswordStrength;
import com.sunbird.core.common.helper.PasswordStrengthWrapper;

/**
 * SecurityUtil.java, Used to access spring security
 * 

 * @since 21/05/2017
 **/
@Component
public class SecurityUtil {

	public static Long DEFAULT_TENANT;
	public static Long DEFAULT_BRANCH;
	public static Long APPLICATION_ADMIN;
	private static final SecureRandom RANDOM = new SecureRandom();
	private static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();
	private static final BasicTextEncryptor TEXT_ENCRYPTOR = new BasicTextEncryptor();
	private static final String ADMIN = "ADMIN";

	@Value("${system.defaultTenant}")
	private String defaultTenant;
	@Value("${system.defaultBranch}")
	private String defaultBranch;
	@Value("${system.admin}")
	private String admin;
	@Value("${system.encryptionKey}")
	private String encryptionKey;

	@PostConstruct
	public void init() {
		DEFAULT_TENANT = new Long(defaultTenant);
		DEFAULT_BRANCH = new Long(defaultBranch);
		APPLICATION_ADMIN = new Long(admin);
		TEXT_ENCRYPTOR.setPasswordCharArray(encryptionKey.toCharArray());//set the encryption key
	}

	/**
	 * One way encryption. Used for passwords without the need to read the original data.
	 * 
	 * @param str
	 * @return encrypted text
	 */
	public static String encode(String str) {
		String hashedPassword = PASSWORD_ENCODER.encode(str);
		return hashedPassword;
	}

	public static String generatePassword() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(StringUtil.getRandomAlphabets(1, Boolean.FALSE));
		stringBuilder.append(StringUtil.getRandomAlphabets(1, Boolean.TRUE));
		stringBuilder.append(StringUtil.getRandomNumbers(1));
		stringBuilder.append(StringUtil.getRandomSpecialChars(1));
		stringBuilder.append(StringUtil.generateRandoms(4,
				'0', '9', 'a', 'z', 'A', 'Z',
				(char) 33, (char) 47, //!"#$%&'()*+,-./
				(char) 58, (char) 64, //:;<=>?@
				(char) 91, (char) 96, //[\]^_`
				(char) 123, (char) 126) //{|}~
		);
		return StringUtil.shuffleString(stringBuilder.toString());
	}

	public static void checkPasswordStrength(PasswordStrengthWrapper passwordStrengthWrapper) {
		List<String> errorsToThrow = getPasswordErrors(passwordStrengthWrapper);
		if (errorsToThrow.size() > 0) {
			throw new BusinessException("Password conditions not met: " + String.join(", ", errorsToThrow),
					"passwordConditionsNotMet", ErrorSeverity.ERROR, errorsToThrow);
		}
	}

	public static List<String> getPasswordErrors(PasswordStrengthWrapper passwordStrengthWrapper) {
		List<String> errorsToThrow = new ArrayList<>();
		if (!passwordStrengthWrapper.getDigits()) {
			errorsToThrow.add("digits");
		}
		if (!passwordStrengthWrapper.getUpper()) {
			errorsToThrow.add("upper");
		}
		if (!passwordStrengthWrapper.getLower()) {
			errorsToThrow.add("lower");
		}
		if (!passwordStrengthWrapper.getSpecial()) {
			errorsToThrow.add("special");
		}
		if (passwordStrengthWrapper.getLength() < 8) {
			errorsToThrow.add("minimum8Chars");
		}
		return errorsToThrow;
	}

	public static PasswordStrengthWrapper getPasswordStrength(String password) {
		if (StringUtil.isEmpty(password)) {
			throw new IllegalArgumentException("Password cannot be empty!");
		}
		PasswordStrengthWrapper strengthWrapper = new PasswordStrengthWrapper();
		strengthWrapper.setLength(password.length());
		Double score = 0.0;

		Map<String, Double> letters = new HashMap<>();
		String[] chars = password.split("");
		for (int i = 0; i < password.length(); i++) {
			if (!letters.containsKey(chars[i])) {
				letters.put(chars[i], 1.0);
			} else {
				letters.put(chars[i], letters.get(chars[i]) + 1);
			}
			score += 5.0 / letters.get(chars[i]);
		}

		int variationCount = 0;
		strengthWrapper.setDigits(Pattern.compile("\\d").matcher(password).find());
		variationCount += strengthWrapper.getDigits() ? 1 : 0;

		strengthWrapper.setLower(Pattern.compile("[a-z]").matcher(password).find());
		variationCount += strengthWrapper.getLower() ? 1 : 0;

		strengthWrapper.setUpper(Pattern.compile("[A-Z]").matcher(password).find());
		variationCount += strengthWrapper.getUpper() ? 1 : 0;

		strengthWrapper.setSpecial(Pattern.compile("[!\"#$%&'()*+,-./:;<=>?@\\[\\\\\\]^_`{|}~]").matcher(password).find());
		variationCount += strengthWrapper.getSpecial() ? 1 : 0;

		score += (variationCount - 1) * 10;
		strengthWrapper.setScore(score);

		if (score > 80) {
			strengthWrapper.setStrength(PasswordStrength.STRONG);
		} else if (score > 60) {
			strengthWrapper.setStrength(PasswordStrength.GOOD);
		} else {
			strengthWrapper.setStrength(PasswordStrength.WEAK);
		}
		return strengthWrapper;
	}

	public static String generatePinCode(int length) {
		if (length < 1) {
			throw new IllegalArgumentException("PinCode length cannot be less than 1");
		}
		int bound = 10;
		for (int i = 0; i < length - 1; i++) {
			bound *= 10;
		}
		int num = RANDOM.nextInt(bound);
		String format = "%0" + length + "d";
		String pinCode = String.format(format, num);
		return pinCode;
	}

	/**
	 * Used with encode for BCrypt Library.
	 * 
	 * @param password
	 * @param encodedPassword
	 * @return true if password match encoded one
	 */
	public static boolean isPasswordsMatch(String password, String encodedPassword) {
		return PASSWORD_ENCODER.matches(password, encodedPassword);
	}

	/**
	 * To encrypt any text. NOT USED FOR LOGIN PASSWORDS
	 * 
	 * @param toEncrypt
	 * @return encrypted text
	 */
	public static String encrypt(String toEncrypt) {
		return TEXT_ENCRYPTOR.encrypt(toEncrypt);
	}

	/**
	 * To decrypt any text. NOT USED FOR LOGIN PASSWORDS
	 * 
	 * @param toEncrypt
	 * @return decrypted text
	 */
	public static String decrypt(String encryptedText) {
		return TEXT_ENCRYPTOR.decrypt(encryptedText);
	}

	/**
	 * Get current authenticated user
	 *
	 * @return User
	 */
	public static UserAccount getCurrentUser() {
		UserAccount user = null;

		try {
			user = (UserAccount) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		} catch (Exception e) {
			throw new BusinessException("USER_NOT_FOUND");
		}

		return user;
	}

	/**
	 * Get current authenticated user If not null else get System internal user
	 *
	 * @return SecUser
	 */
	public static UserAccount getCurrentUserElseInternal() {
		if (isUserLoggedIn()) {
			return getCurrentUser();
		} else {
			return getSystemUser();
		}
	}

	/**
	 * Generate a user.
	 * 
	 * @return User
	 */
	public static UserAccount getSystemUser() {
		UserAccount userAccount = new UserAccountImpl();
		userAccount.setRid(APPLICATION_ADMIN);
		userAccount.setTenantId(DEFAULT_TENANT);
		userAccount.setBranchId(DEFAULT_BRANCH);
		userAccount.setUsername(ADMIN);
		return userAccount;
	}

	/**
	 * Checks whether the argument branchId is allowed for the current user.
	 * A branchId is allowed if the user is tenanted, or it matches a branched user's branchId.
	 *
	 * @param branchId the branchId to check
	 * @return {@code true} if the branchId is allowed;
	 *         {@code false} otherwise.
	 */
	public static Boolean isBranchIdAllowed(Long branchId) {
		Long userBranchId = getCurrentUser().getBranchId();
		if (userBranchId == null || branchId.equals(userBranchId)) {
			return true;
		}
		return false;
	}

	/**
	 * Is the logged in user is the application admin & tenanted.
	 * 
	 * @return true if the logged in user is the application admin and belongs to the default tenant, otherwise false.
	 */
	public static boolean isTenantedApplicationAdmin() {
		UserAccount user = getCurrentUser();
		return user.getRid().equals(APPLICATION_ADMIN) && user.getTenantId().equals(DEFAULT_TENANT);
	}

	/**
	 * Authorize application admin who is tenanted.
	 */
	public static void authorizeTenantedApplicationAdmin() {
		if (!isTenantedApplicationAdmin()) {
			throw new BusinessException("Requires Application Admin", "requiresApplicationAdmin", ErrorSeverity.ERROR);
		}
	}

	/**
	 * Check if user is logged in, hence have a token
	 * 
	 * @return true if user is logged in otherwise false
	 */
	public static boolean isUserLoggedIn() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		//(authentication.getPrincipal() instanceof User) because spring put a dummy user in it
		return authentication != null && authentication.getPrincipal() != null && (authentication.getPrincipal() instanceof UserAccount);
	}

	/**
	 * Check if user has X right
	 * 
	 * @param right : the name of right
	 * @return true if user has this right otherwise false
	 */
	public static boolean isUserAuthorized(String right) {
		Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
		for (GrantedAuthority grantedAuth : authorities) {
			if (grantedAuth.getAuthority().equals(right)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if user has X right and throws exception if not
	 * 
	 * @param evaluator Must be @Autowired. The PermissionEvaluator used to check permissions. Usually custom implementation
	 * @param right The right to check for
	 */
	public static void isUserAuthorized(PermissionEvaluator evaluator, String right) {
		if (!evaluator.hasPermission(SecurityContextHolder.getContext().getAuthentication(), null, right)) {
			throw new AccessDeniedException("Access Denied Exception");
		}
	}

	public static Set<String> getScopes() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Set<String> scopes = ((OAuth2Authentication) auth).getOAuth2Request().getScope();
		return scopes;
	}

	public static boolean hasScope(String scope) {
		Set<String> scopes = getScopes();
		return scopes.contains(scope);
	}

	/**
	 * Manually login a user. Not the recommended way for handling user logins. USE WITH CAUTION!
	 * Only for special cases. And only for temporary access to one or two functions.
	 * 
	 * @param userAccount
	 * @param credentials : nullable
	 * @param authorities : nullable
	 */
	public static void manualLogin(UserAccount userAccount, Object credentials, Collection<? extends GrantedAuthority> authorities) {
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userAccount, credentials, authorities);
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	/**
	 * Manually login a user. Not the recommended way for handling user logins. USE WITH CAUTION!
	 * Only for special cases. And only for temporary access to one or two functions.
	 * 
	 * @param userRid: Rid of desired user
	 * @param tenantRid: Rid of desired tenant
	 * @param branchRid: Rid of desired branch
	 * @param authorities: List of authorities required for the desired operation
	 */
	public static void manualLogin(Long userRid, Long tenantRid, Long branchRid, List<String> authorities) {
		UserAccount user = new UserAccountImpl();
		user.setRid(userRid);
		user.setTenantId(tenantRid);
		user.setBranchId(branchRid);

		List<? extends GrantedAuthority> authorityList;
		if (CollectionUtil.isCollectionEmpty(authorities)) {
			authorityList = new ArrayList<>();
			user.setPrivileges(new ArrayList<>());
		} else {
			authorityList = authorities.stream().map(s -> new SimpleGrantedAuthority(s)).collect(Collectors.toList());
			user.setPrivileges(authorities);
		}

		manualLogin(user, null, authorityList);
	}

	public static String getToken() {
		final OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getDetails();
        return details.getTokenValue();
	}
}
