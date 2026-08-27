package ru.jadegg2568.Postify.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import ru.jadegg2568.Postify.auth.LoginRequest;
import ru.jadegg2568.Postify.auth.RegisterRequest;
import ru.jadegg2568.Postify.common.exception.ParamCodes;
import ru.jadegg2568.Postify.user.UserParamLimits;
import ru.jadegg2568.Postify.user.UpdateProfileRequest;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class RequestValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Nested
    @DisplayName("RegisterRequest validation tests")
    class RegisterRequestValidationTests {

        @Test
        @DisplayName("should pass validation when all fields are valid")
        void registerRequest_ShouldBeValid_WhenAllFieldsAreValid() {
            // given
            RegisterRequest request = new RegisterRequest(
                    "valid@example.com",
                    "Pass123!",
                    "Jo",
                    "John Doe",
                    "A brief description"
            );

            // when
            Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("should fail validation when email is invalid")
        void registerRequest_ShouldFail_WhenEmailIsInvalid() {
            // given
            RegisterRequest request = new RegisterRequest(
                    "invalid-email",
                    "Pass123!",
                    "Jo",
                    "John Doe",
                    null
            );

            // when
            Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isNotEmpty();
            assertThat(violations).extracting(ConstraintViolation::getMessage)
                    .contains(ParamCodes.INVALID_CHARACTERS);
        }

        @Test
        @DisplayName("should fail validation when email is too long")
        void registerRequest_ShouldFail_WhenEmailIsTooLong() {
            // given
            String longEmail = "a".repeat(UserParamLimits.Max.MAIL + 1) + "@example.com";
            RegisterRequest request = new RegisterRequest(
                    longEmail,
                    "Pass123!",
                    "Jo",
                    "John Doe",
                    null
            );

            // when
            Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isNotEmpty();
            assertThat(violations).extracting(ConstraintViolation::getMessage)
                    .contains(ParamCodes.INVALID_SIZE);
        }

        @Test
        @DisplayName("should fail validation when email is too short")
        void registerRequest_ShouldFail_WhenEmailIsTooShort() {
            // given
            RegisterRequest request = new RegisterRequest(
                    "a@b.c",
                    "Pass123!",
                    "Jo",
                    "John Doe",
                    null
            );

            // when
            Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isNotEmpty();
        }

        @Test
        @DisplayName("should fail validation when password doesn't contain special character")
        void registerRequest_ShouldFail_WhenPasswordLacksSpecialCharacter() {
            // given
            RegisterRequest request = new RegisterRequest(
                    "valid@example.com",
                    "Password123",
                    "Jo",
                    "John Doe",
                    null
            );

            // when
            Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isNotEmpty();
            assertThat(violations).extracting(ConstraintViolation::getMessage)
                    .contains(ParamCodes.INVALID_CHARACTERS);
        }

        @Test
        @DisplayName("should fail validation when password doesn't contain digit")
        void registerRequest_ShouldFail_WhenPasswordLacksDigit() {
            // given
            RegisterRequest request = new RegisterRequest(
                    "valid@example.com",
                    "Password!@#",
                    "Jo",
                    "John Doe",
                    null
            );

            // when
            Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isNotEmpty();
            assertThat(violations).extracting(ConstraintViolation::getMessage)
                    .contains(ParamCodes.INVALID_CHARACTERS);
        }

        @Test
        @DisplayName("should fail validation when password doesn't contain letter")
        void registerRequest_ShouldFail_WhenPasswordLacksLetter() {
            // given
            RegisterRequest request = new RegisterRequest(
                    "valid@example.com",
                    "123456!@#",
                    "Jo",
                    "John Doe",
                    null
            );

            // when
            Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isNotEmpty();
            assertThat(violations).extracting(ConstraintViolation::getMessage)
                    .contains(ParamCodes.INVALID_CHARACTERS);
        }

        @Test
        @DisplayName("should fail validation when password is too short")
        void registerRequest_ShouldFail_WhenPasswordIsTooShort() {
            // given
            RegisterRequest request = new RegisterRequest(
                    "valid@example.com",
                    "Pa1!",
                    "Jo",
                    "John Doe",
                    null
            );

            // when
            Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isNotEmpty();
        }

        @Test
        @DisplayName("should fail validation when password is too long")
        void registerRequest_ShouldFail_WhenPasswordIsTooLong() {
            // given
            String longPassword = "Pa1!" + "a".repeat(UserParamLimits.Max.PASSWORD);
            RegisterRequest request = new RegisterRequest(
                    "valid@example.com",
                    longPassword,
                    "Jo",
                    "John Doe",
                    null
            );

            // when
            Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isNotEmpty();
            assertThat(violations).extracting(ConstraintViolation::getMessage)
                    .contains(ParamCodes.INVALID_SIZE);
        }

        @Test
        @DisplayName("should fail validation when name is too short")
        void registerRequest_ShouldFail_WhenNameIsTooShort() {
            // given
            RegisterRequest request = new RegisterRequest(
                    "valid@example.com",
                    "Pass123!",
                    "J",
                    "John Doe",
                    null
            );

            // when
            Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isNotEmpty();
        }

        @Test
        @DisplayName("should fail validation when name is too long")
        void registerRequest_ShouldFail_WhenNameIsTooLong() {
            // given
            String longName = "J".repeat(UserParamLimits.Max.NAME + 1);
            RegisterRequest request = new RegisterRequest(
                    "valid@example.com",
                    "Pass123!",
                    longName,
                    "John Doe",
                    null
            );

            // when
            Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isNotEmpty();
            assertThat(violations).extracting(ConstraintViolation::getMessage)
                    .contains(ParamCodes.INVALID_SIZE);
        }

        @Test
        @DisplayName("should fail validation when displayName is too long")
        void registerRequest_ShouldFail_WhenDisplayNameIsTooLong() {
            // given
            String longDisplayName = "J".repeat(UserParamLimits.Max.DISPLAY_NAME + 1);
            RegisterRequest request = new RegisterRequest(
                    "valid@example.com",
                    "Pass123!",
                    "Jo",
                    longDisplayName,
                    null
            );

            // when
            Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isNotEmpty();
            assertThat(violations).extracting(ConstraintViolation::getMessage)
                    .contains(ParamCodes.INVALID_SIZE);
        }

        @Test
        @DisplayName("should fail validation when description is too long")
        void registerRequest_ShouldFail_WhenDescriptionIsTooLong() {
            // given
            String longDescription = "D".repeat(UserParamLimits.Max.DESCRIPTION + 1);
            RegisterRequest request = new RegisterRequest(
                    "valid@example.com",
                    "Pass123!",
                    "Jo",
                    "John Doe",
                    longDescription
            );

            // when
            Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isNotEmpty();
            assertThat(violations).extracting(ConstraintViolation::getMessage)
                    .contains(ParamCodes.INVALID_SIZE);
        }
    }

    @Nested
    @DisplayName("LoginRequest validation tests")
    class LoginRequestValidationTests {

        @Test
        @DisplayName("should pass validation when all fields are valid")
        void loginRequest_ShouldBeValid_WhenAllFieldsAreValid() {
            // given
            LoginRequest request = new LoginRequest(
                    "validlogin",
                    "Pass123!"
            );

            // when
            Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("should fail validation when login is too short")
        void loginRequest_ShouldFail_WhenLoginIsTooShort() {
            // given
            LoginRequest request = new LoginRequest(
                    "short",
                    "Pass123!"
            );

            // when
            Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isNotEmpty();
        }

        @Test
        @DisplayName("should fail validation when login is too long")
        void loginRequest_ShouldFail_WhenLoginIsTooLong() {
            // given
            String longLogin = "L".repeat(UserParamLimits.Max.LOGIN + 1);
            LoginRequest request = new LoginRequest(
                    longLogin,
                    "Pass123!"
            );

            // when
            Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isNotEmpty();
            assertThat(violations).extracting(ConstraintViolation::getMessage)
                    .contains(ParamCodes.INVALID_SIZE);
        }

        @Test
        @DisplayName("should fail validation when password is too short")
        void loginRequest_ShouldFail_WhenPasswordIsTooShort() {
            // given
            LoginRequest request = new LoginRequest(
                    "validlogin",
                    "short"
            );

            // when
            Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isNotEmpty();
        }

        @Test
        @DisplayName("should fail validation when password is too long")
        void loginRequest_ShouldFail_WhenPasswordIsTooLong() {
            // given
            String longPassword = "P".repeat(UserParamLimits.Max.PASSWORD + 1);
            LoginRequest request = new LoginRequest(
                    "validlogin",
                    longPassword
            );

            // when
            Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isNotEmpty();
            assertThat(violations).extracting(ConstraintViolation::getMessage)
                    .contains(ParamCodes.INVALID_SIZE);
        }
    }

    @Nested
    @DisplayName("UpdateProfileRequest validation tests")
    class UpdateProfileRequestValidationTests {

        @Test
        @DisplayName("should pass validation when all fields are valid")
        void updateProfileRequest_ShouldBeValid_WhenAllFieldsAreValid() {
            // given
            UpdateProfileRequest request = new UpdateProfileRequest(
                    "Jo",
                    "John Doe",
                    "A brief description"
            );

            // when
            Set<ConstraintViolation<UpdateProfileRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("should pass validation when all fields are null")
        void updateProfileRequest_ShouldBeValid_WhenAllFieldsAreNull() {
            // given
            UpdateProfileRequest request = new UpdateProfileRequest(
                    null,
                    null,
                    null
            );

            // when
            Set<ConstraintViolation<UpdateProfileRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("should fail validation when name is too short")
        void updateProfileRequest_ShouldFail_WhenNameIsTooShort() {
            // given
            UpdateProfileRequest request = new UpdateProfileRequest(
                    "J",
                    "John Doe",
                    null
            );

            // when
            Set<ConstraintViolation<UpdateProfileRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isNotEmpty();
        }

        @Test
        @DisplayName("should fail validation when name is too long")
        void updateProfileRequest_ShouldFail_WhenNameIsTooLong() {
            // given
            String longName = "J".repeat(UserParamLimits.Max.NAME + 1);
            UpdateProfileRequest request = new UpdateProfileRequest(
                    longName,
                    "John Doe",
                    null
            );

            // when
            Set<ConstraintViolation<UpdateProfileRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isNotEmpty();
            assertThat(violations).extracting(ConstraintViolation::getMessage)
                    .contains(ParamCodes.INVALID_SIZE);
        }

        @Test
        @DisplayName("should fail validation when displayName is too long")
        void updateProfileRequest_ShouldFail_WhenDisplayNameIsTooLong() {
            // given
            String longDisplayName = "D".repeat(UserParamLimits.Max.DISPLAY_NAME + 1);
            UpdateProfileRequest request = new UpdateProfileRequest(
                    "Jo",
                    longDisplayName,
                    null
            );

            // when
            Set<ConstraintViolation<UpdateProfileRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isNotEmpty();
            assertThat(violations).extracting(ConstraintViolation::getMessage)
                    .contains(ParamCodes.INVALID_SIZE);
        }

        @Test
        @DisplayName("should fail validation when description is too long")
        void updateProfileRequest_ShouldFail_WhenDescriptionIsTooLong() {
            // given
            String longDescription = "D".repeat(UserParamLimits.Max.DESCRIPTION + 1);
            UpdateProfileRequest request = new UpdateProfileRequest(
                    "Jo",
                    "John Doe",
                    longDescription
            );

            // when
            Set<ConstraintViolation<UpdateProfileRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isNotEmpty();
            assertThat(violations).extracting(ConstraintViolation::getMessage)
                    .contains(ParamCodes.INVALID_SIZE);
        }
    }
}
