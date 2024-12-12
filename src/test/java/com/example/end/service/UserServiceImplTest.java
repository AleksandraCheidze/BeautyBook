package com.example.end.service;

import com.example.end.dto.*;
import com.example.end.exceptions.RestException;
import com.example.end.exceptions.UserNotFoundException;
import com.example.end.infrastructure.mail.ProjectMailSender;
import com.example.end.mapping.UserMapper;
import com.example.end.models.Category;
import com.example.end.models.User;
import com.example.end.repository.CategoryRepository;
import com.example.end.repository.UserRepository;
import com.example.end.infrastructure.security.sec_servivce.TokenService;
import com.example.end.util.TestDataGenerator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Nested;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Class name: UserServiceImplTest
 * Description:
 *
 * @author Ganna Bieliaieva
 * @since 02/11/2024
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepositoryMocked;
    @Mock
    private CategoryRepository categoryRepositoryMocked;
    @Mock
    private UserMapper userMapperMocked;
    @Mock
    private PasswordEncoder passwordEncoderMocked;
    @Mock
    private ProjectMailSender mailSenderMocked;
    @Mock
    private TokenService tokenServiceMocked;
    @InjectMocks
    private UserServiceImpl userServiceMocked;

    private final User client = new User(1L,"clientFirstName", "clientLastName", "email@testClient.de", true, User.Role.CLIENT);
    private final UserDto userMasterDto = new UserDto(2L, "masterFirstName", "masterLastName", "hashPassword", "email@testMaster.de", User.Role.MASTER, "accessToken", "refreshToken");
    private final UserDto userClientDto = new UserDto(1L, "clientFirstName", "clientLastName", "hashPassword", "email@testClient.de", User.Role.CLIENT, "accessToken", "refreshToken");
    private final User master = new User(2L,"masterFirstName", "masterLastName", "email@testMaster.de", true, User.Role.MASTER);
    private final NewUserDto newUserDto = new NewUserDto();
    private final UserDetailsDto userDetailsDto = new UserDetailsDto();
    private final NewUserDetailsDto newUserDetailsDto = new NewUserDetailsDto();
    private final UserDetailsDto userDetailsDto1 = new UserDetailsDto();
    private final String password = "password";
    private final String masterEmail = "email@testMaster.de";
    private final String clientEmail = "email@testClient.de";
    Long clientId = 1L;
    Long masterId = 2L;
    User.Role masterRole =  User.Role.MASTER;
    User.Role clientRole =  User.Role.CLIENT;
    ProfileImageDto profileImageUrl = new ProfileImageDto("clientProfileImageUrl");
    PortfolioImageDto portfolioImageDto = new PortfolioImageDto();

    @Nested
    @DisplayNameGeneration(ReplaceUnderscores.class)
    public class RegisterUser_Tests {

        @BeforeEach
        void setUp() {
            newUserDto.setHashPassword("password");
            newUserDto.setFirstName("UserFirstName");
            newUserDto.setLastName("UserLastName");
        }

        @Test
        void register_return_userDto_with_role_master_registered_successfully() {
            newUserDto.setEmail(masterEmail);
            newUserDto.setRole(User.Role.MASTER);

            master.setEmail(masterEmail);
            master.setRole(User.Role.MASTER);
            master.setFirstName("UserFirstName");
            master.setLastName("UserLastName");
            master.setHashPassword("hashedPassword");
            master.setActive(false);

            userMasterDto.setEmail(masterEmail);
            userMasterDto.setRole(User.Role.MASTER);

            when(userRepositoryMocked.existsByEmail(masterEmail)).thenReturn(false);
            when(passwordEncoderMocked.encode("password")).thenReturn("hashedPassword");
            when(userRepositoryMocked.save(any(User.class))).thenReturn(master);
            when(userMapperMocked.toDto(master)).thenReturn(userMasterDto);


            UserDto userDto = userServiceMocked.register(newUserDto);

            verify(userRepositoryMocked, times(1)).existsByEmail(masterEmail);
            verify(passwordEncoderMocked, times(1)).encode("password");

           // verify(mailSenderMocked, times(1)).sendEmail(masterEmail, "Bestätigung der Registrierung des Meisters ausstehend", "Ihre Registrierung als Meister wurde erfasst und wartet auf die Bestätigung durch den Administrator. " +
             //       "Wir werden uns mit Ihnen in Verbindung setzen, sobald Ihr Konto bestätigt wurde. Vielen Dank für Ihre Registrierung!");
           // verify(mailSenderMocked, times(0)).sendEmail(masterEmail, "Registrierung auf der Website", "Herzlichen Glückwunsch zur erfolgreichen Registrierung auf unserer Website!");
            verify(mailSenderMocked, times(1)).sendConfirmationEmails(any(), any()); //MASTER
            verify(mailSenderMocked, never()).sendRegistrationEmail(any()); ///CLIENT


            verify(userRepositoryMocked, times(1)).save(any(User.class));

            verify(userMapperMocked, times(1)).toDto(any(User.class));
            verify(tokenServiceMocked, times(1)).generateAccessToken(master);
            verify(tokenServiceMocked, times(1)).generateRefreshToken(master);

            assertEquals(userMasterDto.getRole(), userDto.getRole());
            assertEquals(userMasterDto.getEmail(), userDto.getEmail());
            assertFalse(master.isActive());
        }

        @Test
        void register_return_userDto_with_role_client_registered_successfully() {
            newUserDto.setEmail(clientEmail);
            newUserDto.setRole(User.Role.CLIENT);

            client.setHashPassword("hashedPassword");

            when(userRepositoryMocked.existsByEmail(clientEmail)).thenReturn(false);
            when(passwordEncoderMocked.encode("password")).thenReturn("hashedPassword");
            when(userRepositoryMocked.save(any(User.class))).thenReturn(client);
            when(userMapperMocked.toDto(client)).thenReturn(userClientDto);

            UserDto userDto = userServiceMocked.register(newUserDto);

            verify(userRepositoryMocked, times(1)).existsByEmail(clientEmail);
            verify(passwordEncoderMocked, times(1)).encode("password");

            //verify(mailSenderMocked, times(0)).sendEmail(clientEmail, "Bestätigung der Registrierung des Meisters ausstehend", "Ihre Registrierung als Meister wurde erfasst und wartet auf die Bestätigung durch den Administrator. " +
                 //   "Wir werden uns mit Ihnen in Verbindung setzen, sobald Ihr Konto bestätigt wurde. Vielen Dank für Ihre Registrierung!");
            //verify(mailSenderMocked, times(1)).sendEmail(clientEmail, "Registrierung auf der Website", "Herzlichen Glückwunsch zur erfolgreichen Registrierung auf unserer Website!");
            verify(mailSenderMocked, never()).sendConfirmationEmails(any(), any());
            verify(mailSenderMocked, times(1)).sendRegistrationEmail(any()); ///CLIENT

            verify(userRepositoryMocked, times(1)).save(any(User.class));

            verify(userMapperMocked, times(1)).toDto(any(User.class));
            verify(tokenServiceMocked, times(1)).generateAccessToken(client);
            verify(tokenServiceMocked, times(1)).generateRefreshToken(client);

            assertEquals(userClientDto.getRole(), userDto.getRole());
            assertEquals(userClientDto.getEmail(), userDto.getEmail());
            assertTrue(client.isActive());
        }

        @Test
        public void register_User_throws_RestException_Conflict() {
            newUserDto.setEmail(masterEmail);
            when(userRepositoryMocked.existsByEmail(masterEmail)).thenReturn(true);

            RestException e = assertThrows(RestException.class, () -> userServiceMocked.register(newUserDto));
            assertEquals(HttpStatus.CONFLICT, e.getStatus());
            assertEquals("User with email <email@testMaster.de> already exists", e.getMessage());

            verify(userRepositoryMocked, times(1)).existsByEmail(masterEmail);
            verify(passwordEncoderMocked, never()).encode(any());
            verify(userRepositoryMocked, never()).save(any(User.class));
            verify(userMapperMocked, never()).toDto(any());
            verify(mailSenderMocked, never()).sendEmail(any(), any(), any());
            verify(mailSenderMocked, never()).sendMasterConfirmationRequest(anyString(), anyString());
            verify(tokenServiceMocked, never()).generateAccessToken(master);
            verify(tokenServiceMocked, never()).generateRefreshToken(master);
        }
    }

    @Nested
    @DisplayNameGeneration(ReplaceUnderscores.class)
    class Authenticate_Tests {
        @Test
        void authenticate_return_userDto_successful() {
            client.setHashPassword(password);

            when(userRepositoryMocked.findByEmail(clientEmail)).thenReturn(Optional.of(client));
            when(passwordEncoderMocked.matches(password, "password")).thenReturn(true);
            when(userMapperMocked.toDto(client)).thenReturn(userClientDto);

            UserDto userDto = userServiceMocked.authenticate(clientEmail, password);
            verify(userRepositoryMocked, times(1)).findByEmail(clientEmail);
            verify(passwordEncoderMocked, times(1)).matches(any(), any());
            verify(userMapperMocked, times(1)).toDto(any(User.class));
            assertEquals(userClientDto, userDto);

        }
        @Test
        void authenticate_throws_RestException_incorrect_email() {

            when(userRepositoryMocked.findByEmail(clientEmail)).thenReturn(Optional.empty());

            RestException e = assertThrows(RestException.class, () -> userServiceMocked.authenticate(clientEmail, password));
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus());
            assertEquals("Invalid email <" + clientEmail + ">", e.getMessage());

            verify(userRepositoryMocked, times(1)).findByEmail(clientEmail);
            verify(passwordEncoderMocked, never()).matches(any(), any());
            verify(userMapperMocked, never()).toDto(any(User.class));

        }
        @Test
        void authenticate_throws_RestException_incorrect_password() {
            client.setHashPassword("hashedPassword");
            client.setEmail(clientEmail);

            when(userRepositoryMocked.findByEmail(clientEmail)).thenReturn(Optional.of(client));
            when(passwordEncoderMocked.matches(password, client.getHashPassword())).thenReturn(false);

            RestException e = assertThrows(RestException.class, () -> userServiceMocked.authenticate(clientEmail, password));
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatus());
            assertEquals("Invalid email <" + password + ">", e.getMessage());

            verify(userRepositoryMocked, times(1)).findByEmail(clientEmail);
            verify(passwordEncoderMocked, times(1)).matches(any(), any());
            verify(userMapperMocked, never()).toDto(any(User.class));

        }
    }

    @Nested
    @DisplayNameGeneration(ReplaceUnderscores.class)
    class GetById_Tests {

        @Test
        void getById_return_user_successful() {
            when(userRepositoryMocked.findById(clientId)).thenReturn(Optional.of(client));
            when(userMapperMocked.userDetailsToDto(client)).thenReturn(userDetailsDto);
            UserDetailsDto userDto = userServiceMocked.getById(1L);
            verify(userRepositoryMocked, times(1)).findById(clientId);
            verify(userMapperMocked, times(1)).userDetailsToDto(any(User.class));
            assertEquals(userDetailsDto.getId(), userDto.getId());
            assertEquals(userDetailsDto.getEmail(), userDto.getEmail());
        }

        @Test
        void getById_throws_UserNotFoundException_User_not_found() {
            when(userRepositoryMocked.findById(clientId)).thenReturn(Optional.empty());

            UserNotFoundException e = assertThrows(UserNotFoundException.class, () -> userServiceMocked.getById(clientId));
            assertEquals("User not found for id: 1", e.getMessage());
            verify(userRepositoryMocked, times(1)).findById(clientId);
            verify(userMapperMocked, never()).userDetailsToDto(any(User.class));
        }

    }

    @Nested
    @DisplayNameGeneration(ReplaceUnderscores.class)
    class ValidateEmail_Tests {
        @Test
        void validateEmail_email_was_validated_successfully() {
            when(userRepositoryMocked.existsByEmail(clientEmail)).thenReturn(false);
            userServiceMocked.validateEmail(clientEmail);
            verify(userRepositoryMocked, times(1)).existsByEmail("email@testClient.de");
        }
        @Test
        void validateEmail_throws_RestException() {
            when(userRepositoryMocked.existsByEmail(clientEmail)).thenReturn(true);
            RestException e = assertThrows(RestException.class, () -> userServiceMocked.validateEmail(clientEmail));
            assertEquals(HttpStatus.CONFLICT, e.getStatus());
            assertEquals("User with email <email@testClient.de> already exists", e.getMessage());
            verify(userRepositoryMocked, times(1)).existsByEmail("email@testClient.de");
        }
    }

    @Nested
    @DisplayNameGeneration(ReplaceUnderscores.class)
    class UpdateUserDetails {
        @Test
        void updateUserDetails_throws_UserNotFoundException_users_not_exist() {

                when(userRepositoryMocked.findById(clientId)).thenReturn(Optional.empty());

                UserNotFoundException e = assertThrows(UserNotFoundException.class, () -> userServiceMocked.updateUserDetails(clientId, newUserDetailsDto));
                assertEquals("User not found for id: 1", e.getMessage());

                verify(userRepositoryMocked, times(1)).findById(clientId);
                verify(categoryRepositoryMocked, never()).findAllById(any());
                verify(userRepositoryMocked, never()).save(any());
                verify(userMapperMocked, never()).userDetailsToDto(any());
        }
        //TODO after refactoring the method
        @Test
        void updateUserDetails_throws_ProcedureNotFoundException_procedure_not_exist() {

        }
        //TODO after refactoring the method
        @Test
        void updateUserDetails_return_Updated_UserDetailsDto_successful() {

        }
    }

    @Nested
    @DisplayNameGeneration(ReplaceUnderscores.class)
    class GetMasterById_Tests {

        @Test
        void getMasterById_return_user() {
            when(userRepositoryMocked.findByIdAndRole(masterId, masterRole)).thenReturn(Optional.of(master));
            when(userMapperMocked.toDto(master)).thenReturn(userMasterDto);

            UserDto userDto = userServiceMocked.getMasterById(masterId);

            verify(userRepositoryMocked, times(1)).findByIdAndRole(any(), any());
            verify(userMapperMocked, times(1)).toDto(any(User.class));
            assertEquals(userMasterDto.getId(), userDto.getId());
            assertEquals(userMasterDto.getRole(), userDto.getRole());
        }

        @Test
        void getMasterById_throws_UserNotFoundException_User_not_found() {
            when(userRepositoryMocked.findByIdAndRole(masterId, masterRole)).thenReturn(Optional.empty());

            UserNotFoundException e = assertThrows(UserNotFoundException.class, () -> userServiceMocked.getMasterById(masterId));
            assertEquals("Master not found for id: 2", e.getMessage());
            verify(userRepositoryMocked, times(1)).findByIdAndRole(any(), any());
            verify(userMapperMocked, never()).toDto(any(User.class));
        }
    }

    @Nested
    @DisplayNameGeneration(ReplaceUnderscores.class)
    class GetClientById_Tests {

        @Test
        void getClientById_return_user() {
            when(userRepositoryMocked.findByIdAndRole(clientId, clientRole)).thenReturn(Optional.of(client));
            when(userMapperMocked.toDto(client)).thenReturn(userClientDto);

            UserDto userDto = userServiceMocked.getClientById(clientId);

            verify(userRepositoryMocked, times(1)).findByIdAndRole(any(), any());
            verify(userMapperMocked, times(1)).toDto(any(User.class));
            assertEquals(userClientDto.getId(), userDto.getId());
            assertEquals(userClientDto.getRole(), userDto.getRole());
        }

        @Test
        void getClientById_throws_UserNotFoundException_User_not_found() {
            when(userRepositoryMocked.findByIdAndRole(clientId, clientRole)).thenReturn(Optional.empty());

            UserNotFoundException e = assertThrows(UserNotFoundException.class, () -> userServiceMocked.getClientById(clientId));
            assertEquals("Client not found for id: 1", e.getMessage());
            verify(userRepositoryMocked, times(1)).findByIdAndRole(any(), any());
            verify(userMapperMocked, never()).toDto(any(User.class));
        }

    }
    @Nested
    @DisplayNameGeneration(ReplaceUnderscores.class)
    class ConfirmMasterByEmail_Tests {
        @Test
        void confirmMasterByEmail_activate_and_send_email () {
            master.setRole(User.Role.MASTER);
            master.setActive(false);
            when(userRepositoryMocked.findByEmail(masterEmail)).thenReturn(Optional.of(master));

            userServiceMocked.confirmMasterByEmail(masterEmail);
            verify(mailSenderMocked, times(1)).sendRegistrationEmail(master);
            verify(userRepositoryMocked, times(1)).save(master);
            assertTrue(master.isActive());
        }
        @Test
        void confirmMasterByEmail_throws_UserNotFoundException_master_by_wrong_email_not_found() {
            when(userRepositoryMocked.findByEmail(masterEmail)).thenReturn(Optional.empty());

            UserNotFoundException e = assertThrows(UserNotFoundException.class, () -> userServiceMocked.confirmMasterByEmail(masterEmail));
            assertEquals("Master user not found or already confirmed for email: email@testMaster.de", e.getMessage());
            verify(mailSenderMocked, never()).sendRegistrationEmail(master);
            verify(userRepositoryMocked, never()).save(master);
        }
        @Test
        void confirmMasterByEmail_throws_UserNotFoundException_master_by_wrong_role_not_found() {
            master.setRole(User.Role.CLIENT);
            master.setActive(false);
            when(userRepositoryMocked.findByEmail(masterEmail)).thenReturn(Optional.of(master));

            UserNotFoundException e = assertThrows(UserNotFoundException.class, () -> userServiceMocked.confirmMasterByEmail(masterEmail));
            assertEquals("Master user not found or already confirmed for email: email@testMaster.de", e.getMessage());
            verify(mailSenderMocked, never()).sendRegistrationEmail(master);
            verify(userRepositoryMocked, never()).save(master);
        }
        @Test
        void confirmMasterByEmail_throws_UserNotFoundException_master_has_been_already_confirmed() {
            master.setRole(User.Role.MASTER);
            master.setActive(true);
            when(userRepositoryMocked.findByEmail(masterEmail)).thenReturn(Optional.of(master));

            UserNotFoundException e = assertThrows(UserNotFoundException.class, () -> userServiceMocked.confirmMasterByEmail(masterEmail));
            assertEquals("Master user not found or already confirmed for email: email@testMaster.de", e.getMessage());
            verify(mailSenderMocked, never()).sendRegistrationEmail(master);
            verify(userRepositoryMocked, never()).save(master);
        }
    }

    @Nested
    @DisplayNameGeneration(ReplaceUnderscores.class)
    class FindMasterUserByEmail_Tests {
        @Test
        void findMasterUserByEmail_return_user_successful () {
            master.setRole(User.Role.MASTER);
            master.setActive(false);
            when(userRepositoryMocked.findByEmail(masterEmail)).thenReturn(Optional.of(master));

            User masterUserByEmail = userServiceMocked.findMasterUserByEmail(masterEmail);
            verify(userRepositoryMocked, times(1)).findByEmail(masterEmail);
            assertEquals(master.getId(), masterUserByEmail.getId());
            assertEquals(master.getRole(), masterUserByEmail.getRole());
            assertEquals(master.getEmail(), masterUserByEmail.getEmail());
            assertEquals(master.getFirstName(), masterUserByEmail.getFirstName());

        }
        @Test
        void findMasterUserByEmail_throws_UserNotFoundException_master_by_wrong_email_not_found() {
            when(userRepositoryMocked.findByEmail(masterEmail)).thenReturn(Optional.empty());

            UserNotFoundException e = assertThrows(UserNotFoundException.class, () -> userServiceMocked.findMasterUserByEmail(masterEmail));
            assertEquals("Master user not found or already confirmed for email: email@testMaster.de", e.getMessage());
            verify(userRepositoryMocked, times(1)).findByEmail(masterEmail);

        }
        @Test
        void findMasterUserByEmail_throws_UserNotFoundException_master_by_wrong_role_not_found(){
            master.setRole(User.Role.CLIENT);
            master.setActive(false);
            when(userRepositoryMocked.findByEmail(masterEmail)).thenReturn(Optional.of(master));

            UserNotFoundException e = assertThrows(UserNotFoundException.class, () -> userServiceMocked.findMasterUserByEmail(masterEmail));
            assertEquals("Master user not found or already confirmed for email: email@testMaster.de", e.getMessage());
            verify(userRepositoryMocked, times(1)).findByEmail(masterEmail);
        }

        @Test
        void findMasterUserByEmail_throws_UserNotFoundException_master_is_already_active() {
            master.setRole(User.Role.MASTER);
            master.setActive(true);
            when(userRepositoryMocked.findByEmail(masterEmail)).thenReturn(Optional.of(master));

            UserNotFoundException e = assertThrows(UserNotFoundException.class, () -> userServiceMocked.findMasterUserByEmail(masterEmail));
            assertEquals("Master user not found or already confirmed for email: email@testMaster.de", e.getMessage());
            verify(userRepositoryMocked, times(1)).findByEmail(masterEmail);
        }
    }

    @Nested
    @DisplayNameGeneration(ReplaceUnderscores.class)
    class AddProfileImage_Tests {
        @Test
        void addProfileImage_return_userDetailsDto_profile_image_is_updated_successfully() {
            User clientWithImageUser = new User();
            clientWithImageUser.setProfileImageUrl("clientProfileImageUrl");
            userDetailsDto.setProfileImageUrl("clientProfileImageUrl");

            when(userRepositoryMocked.findById(clientId)).thenReturn(Optional.of(client));
            client.setProfileImageUrl(profileImageUrl.getProfileImageUrl());
            when(userRepositoryMocked.save(client)).thenReturn(clientWithImageUser);
            when(userMapperMocked.userDetailsToDto(clientWithImageUser)).thenReturn(userDetailsDto);

            UserDetailsDto clientWithImageResult = userServiceMocked.addProfileImage(clientId, profileImageUrl);

            verify(userRepositoryMocked, times(1)).findById(clientId);
            verify(userRepositoryMocked, times(1)).save(any(User.class));
            verify(userMapperMocked, times(1)).userDetailsToDto(clientWithImageUser);

            assertNotNull(clientWithImageResult);
            assertEquals(clientWithImageUser.getProfileImageUrl(), clientWithImageResult.getProfileImageUrl());
        }
        @Test
        void addProfileImage_throws_UserNotFoundException_User_not_found() {
            when(userRepositoryMocked.findById(clientId)).thenReturn(Optional.empty());

            UserNotFoundException e = assertThrows(UserNotFoundException.class, () -> userServiceMocked.addProfileImage(clientId, profileImageUrl));
            assertEquals("User not found for id: 1", e.getMessage());

            verify(userRepositoryMocked, times(1)).findById(clientId);
            verify(userRepositoryMocked, never()).save(any(User.class));
            verify(userMapperMocked, never()).userDetailsToDto(any(User.class));

        }
        @Test
        void addProfileImage_throws_IllegalArgumentException_image_URL_is_invalid_empty() {
            ProfileImageDto profileImageInvalidUrl = new ProfileImageDto("");

            IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> userServiceMocked.addProfileImage(clientId, profileImageInvalidUrl));
            assertEquals("Profile image URL must not be null or empty", e.getMessage());

            verify(userRepositoryMocked, never()).findById(clientId);
            verify(userRepositoryMocked, never()).save(any(User.class));
            verify(userMapperMocked, never()).userDetailsToDto(any(User.class));
        }
        @Test
        void addProfileImage_throws_IllegalArgumentException_image_URL_is_invalid_null() {
            ProfileImageDto profileImageInvalidUrl = null;

            IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> userServiceMocked.addProfileImage(clientId, profileImageInvalidUrl));
            assertEquals("Profile image URL must not be null or empty", e.getMessage());

            verify(userRepositoryMocked, never()).findById(clientId);
            verify(userRepositoryMocked, never()).save(any(User.class));
            verify(userMapperMocked, never()).userDetailsToDto(any(User.class));
        }

    }

    @Nested
    @DisplayNameGeneration(ReplaceUnderscores.class)
    class AddPortfolioImages_Tests {
        @Test
        void addPortfolioImages_return_list_of_images_is_updated_successfully() {
            portfolioImageDto.setPortfolioImageUrls(List.of("ImageOne", "ImageTwo","ImageThree"));
            User clientWithImageUser = new User();
            clientWithImageUser.setPortfolioImageUrls(Set.of("ImageOne", "ImageTwo","ImageThree"));
            userDetailsDto.setPortfolioImageUrls(portfolioImageDto.getPortfolioImageUrls());

            when(userRepositoryMocked.findById(clientId)).thenReturn(Optional.of(client));
            client.setPortfolioImageUrls(Set.of("ImageOne", "ImageTwo","ImageThree"));
            when(userRepositoryMocked.save(client)).thenReturn(clientWithImageUser);
            when(userMapperMocked.userDetailsToDto(clientWithImageUser)).thenReturn(userDetailsDto);

            UserDetailsDto clientWithImageResult = userServiceMocked.addPortfolioImages(clientId, portfolioImageDto);

            verify(userRepositoryMocked, times(1)).findById(clientId);
            verify(userRepositoryMocked, times(1)).save(any(User.class));
            verify(userMapperMocked, times(1)).userDetailsToDto(clientWithImageUser);

            assertNotNull(clientWithImageResult);
            assertEquals(clientWithImageResult.getPortfolioImageUrls(), userDetailsDto.getPortfolioImageUrls());
            assertEquals(clientWithImageResult.getPortfolioImageUrls().get(0),"ImageOne");

        }

        @Test
        void addPortfolioImages_throws_UserNotFoundException_User_not_found() {
            when(userRepositoryMocked.findById(clientId)).thenReturn(Optional.empty());

            UserNotFoundException e = assertThrows(UserNotFoundException.class, () -> userServiceMocked.addPortfolioImages(clientId, portfolioImageDto));
            assertEquals("User not found for id: 1", e.getMessage());

            verify(userRepositoryMocked, times(1)).findById(clientId);
            verify(userRepositoryMocked, never()).save(any(User.class));
            verify(userMapperMocked, never()).userDetailsToDto(any(User.class));
        }
        @Test
        void addPortfolioImages_return_userDetails_check_the_uniqueness_of_portfolio_images() {
            portfolioImageDto.setPortfolioImageUrls(List.of("ImageOne", "ImageOne","ImageTwo"));

            User clientWithUpdatedPortfolio = new User();
            userDetailsDto.setPortfolioImageUrls(List.of("ImageOne", "ImageTwo"));

            when(userRepositoryMocked.findById(clientId)).thenReturn(Optional.of(client));
            Set<String> portfolioImages = new HashSet<>(Set.of("ImageOne", "ImageTwo"));

            client.setPortfolioImageUrls(portfolioImages);

            when(userRepositoryMocked.save(client)).thenReturn(clientWithUpdatedPortfolio);
            when(userMapperMocked.userDetailsToDto(clientWithUpdatedPortfolio)).thenReturn(userDetailsDto);

            UserDetailsDto result = userServiceMocked.addPortfolioImages(clientId, portfolioImageDto);

            verify(userRepositoryMocked, times(1)).findById(clientId);
            verify(userRepositoryMocked, times(1)).save(any(User.class));
            verify(userMapperMocked, times(1)).userDetailsToDto(clientWithUpdatedPortfolio);

            assertNotNull(result);
            assertEquals(result.getPortfolioImageUrls(), userDetailsDto.getPortfolioImageUrls());
            assertEquals(result.getPortfolioImageUrls().get(1),"ImageTwo");
            assertEquals(result.getPortfolioImageUrls().size(),2);
        }



    }

    @Nested
    @DisplayNameGeneration(ReplaceUnderscores.class)
    class ActivateMasterUser_Tests {
        @Test
        void activateMasterUser_user_with_role_master_is_activate_successful() {
            master.setActive(false);

            userServiceMocked.activateMasterUser(master);
            assertTrue(master.isActive());
            verify(userRepositoryMocked, times(1)).save(master);
        }

        @Test
        void activateMasterUser_user_with_role_master_is_already_activated() {
            master.setActive(true);

            userServiceMocked.activateMasterUser(master);

            verify(userRepositoryMocked, never()).save(master);
        }

        @Test
        void activateMasterUser_user_with_role_master_is_null() {
            IllegalArgumentException e  = assertThrows(IllegalArgumentException.class, () -> userServiceMocked.activateMasterUser(null));
            assertEquals("Master user cannot be null", e.getMessage());
            verify(userRepositoryMocked, never()).save(master);
        }
    }
    @Nested
    @DisplayNameGeneration(ReplaceUnderscores.class)
    class GetAllMasters_Tests {
        @Test
        void getAllMasters_return_list_of_users_with_role_master_successful() {
            long startTime = System.currentTimeMillis();

            List<User> masters = new ArrayList<>(List.of(master, master));
            userDetailsDto.setFirstName("masterFirstName");
            when(userRepositoryMocked.findAllByRole(masterRole)).thenReturn(masters);
            when(userMapperMocked.userDetailsToDto(master)).thenReturn(userDetailsDto);

            List<UserDetailsDto> resultMasterList = userServiceMocked.getAllMasters();
            verify(userRepositoryMocked, times(1)).findAllByRole(masterRole);
            verify(userMapperMocked, times(2)).userDetailsToDto(any(User.class));

            assertEquals(2, resultMasterList.size());
            assertNotNull(resultMasterList);
            assertEquals("masterFirstName", resultMasterList.get(0).getFirstName());
            assertEquals("masterFirstName", resultMasterList.get(1).getFirstName());

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            System.out.println("Test, which returned a list with two users, executed in: " + duration + " milliseconds");
        }
        @Test
        void getAllMasters_return_empty_list_of_users_with_role_master_successful() {

            when(userRepositoryMocked.findAllByRole(masterRole)).thenReturn(Collections.emptyList());

            List<UserDetailsDto> resultMasterList = userServiceMocked.getAllMasters();
            verify(userRepositoryMocked, times(1)).findAllByRole(masterRole);
            verify(userMapperMocked, never()).userDetailsToDto(any(User.class));

            assertTrue(resultMasterList.isEmpty());
            assertNotNull(resultMasterList);
        }
        @Test
        void getAllMasters_return_big_list_of_users_with_role_master_successful() {
            long startTime = System.currentTimeMillis();
            int countUsers = 10000;

            List<User> masters = TestDataGenerator.generateMockMasters(countUsers);

            when(userRepositoryMocked.findAllByRole(masterRole)).thenReturn(masters);

            List<UserDetailsDto> resultMasterList = userServiceMocked.getAllMasters();

            verify(userRepositoryMocked, times(1)).findAllByRole(masterRole);
            verify(userMapperMocked, times(countUsers)).userDetailsToDto(any(User.class));

            assertEquals(countUsers, resultMasterList.size());
            assertNotNull(resultMasterList);

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            System.out.println("Test, which returned a list with " + countUsers + " users, executed in: " + duration + " milliseconds");
        }
    }
    @Nested
    @DisplayNameGeneration(ReplaceUnderscores.class)
    class GetAllUsers_Tests {
        @Test
        void getAllUsers_return_list_of_users_successful() {
            List<User> users = new ArrayList<>(List.of(client, master));
            userDetailsDto.setFirstName("masterFirstName");
            userDetailsDto1.setFirstName("clientFirstName");

            when(userRepositoryMocked.findAll()).thenReturn(users);
            when(userMapperMocked.userDetailsToDto(master)).thenReturn(userDetailsDto);
            when(userMapperMocked.userDetailsToDto(client)).thenReturn(userDetailsDto1);

            List<UserDetailsDto> resultUsersList = userServiceMocked.getAllUsers();
            verify(userRepositoryMocked, times(1)).findAll();
            verify(userMapperMocked, times(2)).userDetailsToDto(any(User.class));

            assertEquals(2, resultUsersList.size());
            assertNotNull(resultUsersList);
            assertEquals("clientFirstName", resultUsersList.get(0).getFirstName());
            assertEquals("masterFirstName", resultUsersList.get(1).getFirstName());
        }
        @Test
        void getAllUsers_return_empty_list_of_users_successful() {

            when(userRepositoryMocked.findAll()).thenReturn(Collections.emptyList());

            List<UserDetailsDto> resultUsersList = userServiceMocked.getAllUsers();
            verify(userRepositoryMocked, times(1)).findAll();
            verify(userMapperMocked, never()).userDetailsToDto(any(User.class));

            assertNotNull(resultUsersList);
            assertTrue(resultUsersList.isEmpty());
        }
    }

    @Nested
    @DisplayNameGeneration(ReplaceUnderscores.class)
    class FindUsersByCategoryId_Tests {
        @BeforeEach
        void setUp() {
            Category categoryOne = new Category();
            categoryOne.setId(1L);
            Category categoryTwo = new Category();
            categoryOne.setId(2L);
            Category categoryThree = new Category();
            categoryOne.setId(3L);
            Set<Category> categoriesOneTwo = Set.of(categoryOne, categoryTwo);
            Set<Category> categoriesTwoThree = Set.of(categoryThree, categoryTwo);

            client.setCategories(categoriesOneTwo);
            master.setCategories(categoriesTwoThree);
        }
        @Test
        void findUsersByCategoryId_return_list_of_users_successful_with_each_has_category() {

            List<User> users = new ArrayList<>(List.of(client, master));
            userDetailsDto.setFirstName("masterFirstName");
            userDetailsDto1.setFirstName("clientFirstName");

            when(userRepositoryMocked.findUsersByCategoryId(2L)).thenReturn(users);
            when(userMapperMocked.userDetailsToDto(master)).thenReturn(userDetailsDto);
            when(userMapperMocked.userDetailsToDto(client)).thenReturn(userDetailsDto1);

            List<UserDetailsDto> resultUsersList = userServiceMocked.findUsersByCategoryId(2L);
            verify(userRepositoryMocked, times(1)).findUsersByCategoryId(any());
            verify(userMapperMocked, times(2)).userDetailsToDto(any(User.class));

            assertEquals(2, resultUsersList.size());
            assertNotNull(resultUsersList);
            assertEquals("clientFirstName", resultUsersList.get(0).getFirstName());
            assertEquals("masterFirstName", resultUsersList.get(1).getFirstName());
        }

        @Test
        void findUsersByCategoryId_return_list_of_users_successful_with_one_has_category() {

            List<User> users = new ArrayList<>(List.of(client));
            userDetailsDto1.setFirstName("clientFirstName");

            when(userRepositoryMocked.findUsersByCategoryId(1L)).thenReturn(users);
            when(userMapperMocked.userDetailsToDto(client)).thenReturn(userDetailsDto1);

            List<UserDetailsDto> resultUsersList = userServiceMocked.findUsersByCategoryId(1L);
            verify(userRepositoryMocked, times(1)).findUsersByCategoryId(any());
            verify(userMapperMocked, times(1)).userDetailsToDto(any(User.class));

            assertEquals(1, resultUsersList.size());
            assertNotNull(resultUsersList);
            assertEquals("clientFirstName", resultUsersList.get(0).getFirstName());

        }
        @Test
        void findUsersByCategoryId_throws_UserNotFoundException_when_list_of_users_is_empty() {

            when(userRepositoryMocked.findUsersByCategoryId(1L)).thenReturn(Collections.emptyList());

            UserNotFoundException e = assertThrows(UserNotFoundException.class, () -> userServiceMocked.findUsersByCategoryId(1L));
            assertEquals("User for category with ID 1 not found", e.getMessage());

            verify(userRepositoryMocked, times(1)).findUsersByCategoryId(any());
            verify(userMapperMocked, never()).userDetailsToDto(any(User.class));
        }
    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class FindByEmail_Tests {
        @Test
        void findByEmail_return_User_successful() {
            when(userRepositoryMocked.findByEmail(clientEmail)).thenReturn(Optional.of(client));

            Optional<User> resultUser = userServiceMocked.findByEmail(clientEmail);

            verify(userRepositoryMocked, times(1)).findByEmail(any());

            assertNotNull(resultUser);
            assertTrue(resultUser.isPresent());
            assertEquals(clientEmail, resultUser.get().getEmail());

        }
        @Test
        void findByEmail_return_empty_Optional_User_with_this_Email_not_exist() {
            String notExistEmail = "email@notExistClient.de";
            when(userRepositoryMocked.findByEmail(notExistEmail)).thenReturn(Optional.empty());

            Optional<User> resultUser = userServiceMocked.findByEmail(notExistEmail);

            verify(userRepositoryMocked, times(1)).findByEmail(any());

            assertNotNull(resultUser);
            assertFalse(resultUser.isPresent());

        }

        @Test
        void findByEmail_throws_IllegalArgumentException_when_email_is_null() {

            assertThrows(IllegalArgumentException.class, () -> userServiceMocked.findByEmail(null));

            verify(userRepositoryMocked, never()).findByEmail(any());
        }

        @Test
        void findByEmail_throws_IllegalArgumentException_when_email_is_empty() {

            IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> userServiceMocked.findByEmail(""));
            assertEquals("Email must not be null or empty", e.getMessage());

            verify(userRepositoryMocked, never()).findByEmail(any());
        }

    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class DeleteById_Tests {
        @Test
        void deleteById_user_deleted_successful() {

            when(userRepositoryMocked.findById(clientId)).thenReturn(Optional.of(client));

            userServiceMocked.deleteById(clientId);
            verify(userRepositoryMocked, times(1)).findById(clientId);
            verify(userRepositoryMocked, times(1)).delete(any(User.class));

        }
        @Test
        void deleteById_throws_UserNotFoundException_users_not_exist() {

            when(userRepositoryMocked.findById(clientId)).thenReturn(Optional.empty());

            UserNotFoundException e = assertThrows(UserNotFoundException.class, () -> userServiceMocked.deleteById(clientId));
            assertEquals("User not found for id: 1", e.getMessage());

            verify(userRepositoryMocked, times(1)).findById(clientId);
            verify(userRepositoryMocked, never()).delete(any(User.class));

        }

    }
}
