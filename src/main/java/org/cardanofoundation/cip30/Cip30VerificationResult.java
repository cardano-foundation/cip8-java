package org.cardanofoundation.cip30;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.cardanofoundation.cip30.MoreHex.to;

/**
 * The Cip30VerificationResult contains validation information after parsing / verifying.
 */
@ParametersAreNonnullByDefault
public class Cip30VerificationResult {

    /**
     * Static instance of {@code Logger} used for logging.
     */
    private static final Logger logger = LoggerFactory.getLogger(CIP30Verifier.class);

    /**
     * Provides information in case that the validation of the message fails.
     */
    private Optional<ValidationError> validationError = Optional.of(ValidationError.UNKNOWN);

    /**
     * Optional Cardano address as byte array
     */
    private Optional<byte[]> address = Optional.empty();

    private byte[] ed25519PublicKey;

    private byte[] ed25519Signature;

    /**
     * The actual message encoded in signature field of DataSignature.
     */
    private byte[] message;

    /**
     * This is cose wrapped message (Signature1) that has been directly signed by ED 25519 algorithm.
     */
    private byte[] cosePayload;

    public static class Builder {


        private Optional<ValidationError> validationError = Optional.of(ValidationError.UNKNOWN);

        private Optional<byte[]> address = Optional.empty();

        private byte[] ed25519PublicKey;

        private byte[] ed25519Signature;

        private byte[] message;

        private byte[] cosePayload;

        /**
         * Creates an object {@code Builder} in charge of building the class
         * {@code Cip30VerificationResult}.
         */
        static Builder newBuilder() {
            return new Builder();
        }

        public Builder valid() {
            this.validationError = Optional.empty();
            return Builder.this;
        }

        public Builder validationError(ValidationError error) {
            Objects.requireNonNull(error, "validation error is required");

            this.validationError = Optional.of(error);
            return Builder.this;
        }

        public Builder address(byte[] address) {
            Objects.requireNonNull(address, "address is required");
            this.address = Optional.of(address);
            return Builder.this;
        }

        public Builder ed25519PublicKey(byte[] ed25519PublicKey) {
            Objects.requireNonNull(ed25519PublicKey, "public key is required");
            this.ed25519PublicKey = ed25519PublicKey;
            return Builder.this;
        }

        public Builder ed25519Signature(byte[] signature) {
            Objects.requireNonNull(signature, "signature is required");
            this.ed25519Signature = signature;
            return Builder.this;
        }

        public Builder message(byte[] message) {
            this.message = message;
            return Builder.this;
        }

        public Builder cosePayload(byte[] cosePayload) {
            Objects.requireNonNull(cosePayload, "cosePayload is required");
            this.cosePayload = cosePayload;
            return Builder.this;
        }

        /**
         * Creates an instance of the class {@code Cip30VerificationResult} using the information
         * stored.
         */
        public Cip30VerificationResult build() {
            return new Cip30VerificationResult(this);
        }
    }

    /**
     * Builder's private constructor
     *
     * @param builder
     */
    private Cip30VerificationResult(Builder builder) {
        this.validationError = builder.validationError;
        this.address = builder.address;
        this.ed25519PublicKey = builder.ed25519PublicKey;
        this.ed25519Signature = builder.ed25519Signature;
        this.message = builder.message;
        this.cosePayload = builder.cosePayload;
    }

    /**
     * Checks if CIP-30 DataSignature is valid.
     *
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return validationError.isEmpty();
    }

    /**
     * Returns an optional of the enum {@code ValidationError}.
     *
     * @return if CIP-30 DataSignature is valid returns Optional.empty,
     * if DataSignature is invalid it will return Optional with an actual {@code ValidationError}.
     */
    public Optional<ValidationError> getValidationError() {
        return validationError;
    }

    /**
     * @return optionally present Cardano address
     */
    public Optional<byte[]> getAddress() {
        return address;
    }

    /**
     * @return ED 25519 public key embedded in signature part of DataSignature (CIP-30).
     * Returns null in case CIP-30 DataSignature is invalid.
     */
    public @Nullable byte[] getEd25519PublicKey() {
        return ed25519PublicKey;
    }

    /**
     * @return ED 25519 signature embedded in signature part of DataSignature (CIP-30).
     * Returns null in case CIP-30 DataSignature is invalid.
     */
    public @Nullable byte[] getEd25519Signature() {
        return ed25519Signature;
    }

    /**
     * @return actual signed message, which is embedded in signature part of DataSignature (CIP-30).
     * Returns null in case CIP-30 DataSignature is invalid.
     */
    public @Nullable byte[] getMessage() {
        return message;
    }

    /**
     * @return Signature1 map serialised as bytes, which is embedded in signature part of DataSignature (CIP-30).
     * It will return null in case CIP-30 DataSignature is invalid.
     */
    public @Nullable byte[] getCosePayload() {
        return cosePayload;
    }

    /**
     * Returns the Ed25519 public key in a specific encoding format and charset.
     * <p>
     * The possible encoding formats are defined by the enum {@code Format} and
     * the possible charset by the enum {@code StandardCharsets}.
     *
     * @param f the encoding format wanted for the returned public key.
     * @param c the wanted charset
     * @return the formatted public key or null if CIP-30 DataSignature parsing / validation failed.
     */
    public String getEd25519PublicKey(Format f, Charset c) {
        return formatter(ed25519PublicKey, f, c);
    }

    /**
     * Returns the Ed25519 public key in a specific encoding format and {@code UTF_8} charset.
     * <p>
     * The possible encoding formats are defined by the enum {@code Format} and
     * the charset is assumed to be {@code UTF_8}.
     *
     * @param f The encoding format wanted for the returned Ed25519 public key.
     * @return the Ed25519 public key or null if CIP-30 DataSignature parsing / validation failed.
     */
    public @Nullable String getEd25519PublicKey(Format f) {
        return getEd25519PublicKey(f, UTF_8);
    }

    /**
     * Returns the Ed25519 signature of the message in a specific encoding format and charset.
     * <p>
     * The possible encoding formats are defined by the enum {@code Format} and
     * the possible charset by the enum {@code StandardCharsets}.
     *
     * @param f the encoding format wanted for the returned Ed25519 signature.
     * @param c the wanted charset
     * @return the formatted Ed25519 signature or null if CIP-30 DataSignature parsing / validation failed.
     */
    public @Nullable String getEd25519Signature(Format f, Charset c) {
        return formatter(ed25519Signature, f, c);
    }

    /**
     * Returns the Ed25519 signature of the message in a specific encoding format and
     * {@code UTF_8} charset.
     * <p>
     * The possible encoding formats are defined by the enum {@code Format} and
     * the charset is assumed to be {@code UTF_8}.
     *
     * @param f the encoding format wanted for the returned signature.
     * @return the formatted Ed25519 signature or null if CIP-30 DataSignature parsing / validation failed.
     */
    public @Nullable String getEd25519Signature(Format f) {
        return getEd25519Signature(f, UTF_8);
    }

    /**
     * Returns the message in a specific encoding format and charset.
     * <p>
     * The possible encoding formats are defined by the enum {@code Format} and
     * the possible charset by the enum {@code StandardCharsets}.
     *
     * @param f The encoding format wanted for the returned message.
     * @param c The charset wanted for the returned message.
     * @return the formatted message or null if CIP-30 DataSignature parsing / validation failed.
     */
    public @Nullable String getMessage(Format f, Charset c) {
        return formatter(message, f, c);
    }

    /**
     * Returns the message in a specific encoding format and {@code UTF_8} charset.
     * <p>
     * The possible encoding formats are defined by the enum {@code Format} and
     * the charset is {@code UTF_8}.
     *
     * @param f The encoding format wanted for the returned message.
     * @return the formatted message or null if CIP-30 DataSignature parsing / validation failed.
     */
    public @Nullable String getMessage(Format f) {
        return getMessage(f, UTF_8);
    }

    /**
     * Returns the cose payload in a specific encoding format and {@code UTF_8} charset.
     * <p>
     * The possible encoding formats are defined by the enum {@code Format} and
     * the charset is {@code UTF_8}.
     *
     * @param f The encoding format wanted for the returned cose payload.
     * @return the COSE payload in the provided encoding format.
     */
    public @Nullable String getCosePayload(Format f) {
        return getCosePayload(f, UTF_8);
    }

    /**
     * Returns the cose payload in a specific encoding format and charset.
     * <p>
     * The possible encoding formats are defined by the enum {@code Format} and
     * the possible charset by the enum {@code StandardCharsets}.
     *
     * @param f The encoding format wanted for the returned cose payload.
     * @param c The charset wanted for the returned cose payload.
     * @return the COSE payload in the provided encoding format and charset.
     */
    public @Nullable String getCosePayload(Format f, Charset c) {
        return formatter(cosePayload, f, c);
    }

    /**
     * Creates a new {@code Cip30VerificationResult} containing information of
     * the validation error in case that the information is not valid with the
     * CIP 30 specification.
     *
     * @param error the value of the enum {@code ValidationError} corresponding with
     *              the reason why the information is not valid.
     * @return a new {@code Cip30VerificationResult} containing information of
     * the validation error.
     */
    public static Cip30VerificationResult createInvalid(ValidationError error) {
        return Builder.newBuilder()
                .validationError(error)
                .build();
    }

    /**
     * Changes the format of the information provided.
     * <p>
     * The possible encoding formats are defined by the enum {@code Format} and
     * the possible charset by the enum {@code StandardCharsets}.
     *
     * @param bytes array of bytes with the information to be formatted.
     * @param f     The encoding format wanted for the returned information.
     * @param c     The charset wanted for the returned information.
     * @return array of bytes provided transformed to the encoding and format and
     * charset specified.
     */
    private String formatter(byte[] bytes, Format f, Charset c) {
        if (bytes == null) {
            return null;
        }
        if (f == null) {
            throw new IllegalArgumentException("format must be defined");
        }
        if (c == null) {
            throw new IllegalArgumentException("charset must be defined");
        }

        return switch (f) {
            case HEX -> to(bytes);
            case TEXT -> new String(bytes, c);
            case BASE64 -> Base64.getEncoder().encodeToString(bytes);
        };
    }

    /**
     * Returns a string representation of this {@code Cip30VerificationResult}. This method is
     * intended to be used only for debugging purposes.
     *
     * @return a string representation of this {@code Cip30VerificationResult}.
     */
    @Override
    public String toString() {
        return "Cip30VerificationResult{" +
                "valid=" + validationError.isEmpty() +
                ", validationError=" + validationError +
                ", address=" + address +
                ", ed25519PublicKey=" + ed25519PublicKey +
                ", ed25519Signature=" + ed25519Signature +
                ", message=" + message +
                ", cosePayload=" + cosePayload +
                '}';
    }

}
