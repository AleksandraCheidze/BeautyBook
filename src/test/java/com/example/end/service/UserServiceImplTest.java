package com.example.end.service;

import com.example.end.dto.NewUserDto;
import com.example.end.dto.UserDto;
import com.example.end.exceptions.RestException;
import com.example.end.mail.ProjectMailSender;
import com.example.end.mapping.UserMapper;
import com.example.end.models.User;
import com.example.end.repository.CategoryRepository;
import com.example.end.repository.UserRepository;
import com.example.end.security.sec_servivce.TokenService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
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
 * @since 10/11/2024
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceImplTest {

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

    private final User client = new User();
    private final UserDto userMasterDto = new UserDto();
    private final UserDto userClientDto = new UserDto();
    private final User master = new User();
    private final NewUserDto newUserDto = new NewUserDto();
    private final String existedEmail = "email@testMaster.de";
    private final String masterEmail = "email@testMaster.de";
    private final String clientEmail = "email@testClient.de";

    @Nested
    class Register{
        @Test
        void register() {
            newUserDto.setEmail(masterEmail);
            newUserDto.setRole(User.Role.MASTER);
            newUserDto.setHashPassword("password");
            newUserDto.setFirstName("UserFirstName");
            newUserDto.setLastName("UserLastName");

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

            verify(mailSenderMocked, times(1)).sendEmail(masterEmail, "Bestätigung der Registrierung des Meisters ausstehend", "Ihre Registrierung als Meister wurde erfasst und wartet auf die Bestätigung durch den Administrator. " +
                    "Wir werden uns mit Ihnen in Verbindung setzen, sobald Ihr Konto bestätigt wurde. Vielen Dank für Ihre Registrierung!");
            verify(mailSenderMocked, times(0)).sendEmail(masterEmail, "Registrierung auf der Website", "Herzlichen Glückwunsch zur erfolgreichen Registrierung auf unserer Website!");
            verify(mailSenderMocked, times(1)).sendMasterConfirmationRequest(any(), any());

            verify(userRepositoryMocked, times(1)).save(any(User.class));

            verify(userMapperMocked, times(1)).toDto(any(User.class));
            verify(tokenServiceMocked, times(1)).generateAccessToken(master);
            verify(tokenServiceMocked, times(1)).generateRefreshToken(master);

            assertEquals(userMasterDto.getRole(), userDto.getRole());
            assertEquals(userMasterDto.getEmail(), userDto.getEmail());
            assertFalse(master.isActive());
        }
    }



    @Nested
    @DisplayNameGeneration(ReplaceUnderscores.class)
    class RegisterUser_Tests {
        @Test
        void register_User_with_role_master_return_user() {
            newUserDto.setEmail(masterEmail);
            newUserDto.setRole(User.Role.MASTER);
            newUserDto.setHashPassword("password");
            newUserDto.setFirstName("UserFirstName");
            newUserDto.setLastName("UserLastName");

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

            verify(mailSenderMocked, times(1)).sendEmail(masterEmail, "Bestätigung der Registrierung des Meisters ausstehend", "Ihre Registrierung als Meister wurde erfasst und wartet auf die Bestätigung durch den Administrator. " +
                    "Wir werden uns mit Ihnen in Verbindung setzen, sobald Ihr Konto bestätigt wurde. Vielen Dank für Ihre Registrierung!");
            verify(mailSenderMocked, times(0)).sendEmail(masterEmail, "Registrierung auf der Website", "Herzlichen Glückwunsch zur erfolgreichen Registrierung auf unserer Website!");
            verify(mailSenderMocked, times(1)).sendMasterConfirmationRequest(any(), any());

            verify(userRepositoryMocked, times(1)).save(any(User.class));

            verify(userMapperMocked, times(1)).toDto(any(User.class));
            verify(tokenServiceMocked, times(1)).generateAccessToken(master);
            verify(tokenServiceMocked, times(1)).generateRefreshToken(master);

            assertEquals(userMasterDto.getRole(), userDto.getRole());
            assertEquals(userMasterDto.getEmail(), userDto.getEmail());
            assertFalse(master.isActive());

        }
        @Test
        void register_User_with_role_client_return_user() {
            newUserDto.setEmail(clientEmail);
            newUserDto.setRole(User.Role.CLIENT);
            newUserDto.setHashPassword("password");
            newUserDto.setFirstName("UserFirstName");
            newUserDto.setLastName("UserLastName");

            client.setEmail(clientEmail);
            client.setRole(User.Role.CLIENT);
            client.setFirstName("UserFirstName");
            client.setLastName("UserLastName");
            client.setHashPassword("hashedPassword");
            client.setActive(true);

            userClientDto.setEmail(clientEmail);
            userClientDto.setRole(User.Role.CLIENT);


            when(userRepositoryMocked.existsByEmail(clientEmail)).thenReturn(false);
            when(passwordEncoderMocked.encode("password")).thenReturn("hashedPassword");
            when(userRepositoryMocked.save(any(User.class))).thenReturn(client);
            when(userMapperMocked.toDto(client)).thenReturn(userClientDto);

            UserDto userDto = userServiceMocked.register(newUserDto);


            verify(userRepositoryMocked, times(1)).existsByEmail(clientEmail);
            verify(passwordEncoderMocked, times(1)).encode("password");

            verify(mailSenderMocked, times(0)).sendEmail(clientEmail, "Bestätigung der Registrierung des Meisters ausstehend", "Ihre Registrierung als Meister wurde erfasst und wartet auf die Bestätigung durch den Administrator. " +
                    "Wir werden uns mit Ihnen in Verbindung setzen, sobald Ihr Konto bestätigt wurde. Vielen Dank für Ihre Registrierung!");
            verify(mailSenderMocked, times(1)).sendEmail(clientEmail, "Registrierung auf der Website", "Herzlichen Glückwunsch zur erfolgreichen Registrierung auf unserer Website!");
            verify(mailSenderMocked, times(0)).sendMasterConfirmationRequest(any(), any());

            verify(userRepositoryMocked, times(1)).save(any(User.class));

            verify(userMapperMocked, times(1)).toDto(any(User.class));
            verify(tokenServiceMocked, times(1)).generateAccessToken(client);
            verify(tokenServiceMocked, times(1)).generateRefreshToken(client);

            assertEquals(userClientDto.getRole(), userDto.getRole());
            assertEquals(userClientDto.getEmail(), userDto.getEmail());
            assertTrue(client.isActive());


        }

        @Test
        void register_User_throws_RestException_Conflict() {
            newUserDto.setEmail(existedEmail);
            when(userRepositoryMocked.existsByEmail(existedEmail)).thenReturn(true);

            RestException e = assertThrows(RestException.class, () -> userServiceMocked.register(newUserDto));
            assertEquals(HttpStatus.CONFLICT, e.getStatus());
            assertEquals("User with email <email@testMaster.de> already exists", e.getMessage());

            verify(userRepositoryMocked, times(1)).existsByEmail(existedEmail);
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
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class Authenticate_Tests {
        @Test
        void authenticate_() {

        }

    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class GetById_Tests {
        @Test
        void getById_return_user() {

        }

    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class SendConfirmationEmails_Tests {
        @Test
        void sendConfirmationEmails() {

        }

    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class ValidateEmail_Tests {
        @Test
        void validateEmail_success() {
            when(userRepositoryMocked.existsByEmail(existedEmail)).thenReturn(false);
            userServiceMocked.validateEmail(existedEmail);
            verify(userRepositoryMocked, times(1)).existsByEmail("email@testMaster.de");
        }
        @Test
        void validateEmail_throws_RestException() {
            when(userRepositoryMocked.existsByEmail(existedEmail)).thenReturn(true);
            RestException e = assertThrows(RestException.class, () -> userServiceMocked.validateEmail(existedEmail));
            assertEquals(HttpStatus.CONFLICT, e.getStatus());
            assertEquals("User with email <email@testMaster.de> already exists", e.getMessage());
            verify(userRepositoryMocked, times(1)).existsByEmail("email@testMaster.de");
        }

    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class CreateUser_Tests {
        @Test
        void createUser() {

        }

    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class validateEmail_Tests {
        @Test
        void validateEmail() {

        }

    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class UpdateUserDetails {
        @Test
        void updateUserDetails() {

        }

    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class SendRegistrationEmail_Tests {
        @Test
        void sendRegistrationEmail() {

        }

    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class GetMasterById_Tests {
        @Test
        void getMasterById() {

        }

    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class GetClientById_Tests {
        @Test
        void getClientById() {

        }

    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class ConfirmMasterByEmail_Tests {
        @Test
        void confirmMasterByEmail() {

        }

    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class FindMasterUserByEmail_Tests {
        @Test
        void findMasterUserByEmail() {

        }

    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class AddProfileImage_Tests {
        @Test
        void addProfileImage() {

        }

    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class AddPortfolioImages_Tests {
        @Test
        void addPortfolioImages() {

        }

    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class ActivateMasterUser_Tests {
        @Test
        void activateMasterUser() {

        }

    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class GetAllMasters_Tests {
        @Test
        void getAllMasters() {

        }

    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class GetUserById_Tests {
        @Test
        void getUserById() {

        }

    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class FindUsersByCategoryId_Tests {
        @Test
        void findUsersByCategoryId() {

        }

    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class LoadUserByEmail_Tests {
        @Test
        void loadUserByEmail() {

        }

    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class DeleteById_Tests {
        @Test
        void deleteById() {

        }

    }

    @Nested
    @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
    class SendMessageToAdmin_Tests {
        @Test
        void sendMessageToAdmin() {

        }

    }


}