package gherkin;

import gherkin.ast.Location;

import java.util.Collections;
import java.util.List;

public class ParserException extends RuntimeException {
    public final Location location;

    protected ParserException(String message) {
        super(message);
        location = null;
    }

    protected ParserException(String message, Location location) {
        super(getMessage(message, location));
        this.location = location;
    }

    private static String getMessage(String message, Location location) {
        return String.format("(%s:%s): %s", location.getLine(), location.getColumn(), message);
    }

    public static class AstBuilderException extends ParserException {
        public AstBuilderException(String message, Location location) {
            super(message, location);
        }
    }

    public static class NoSuchLanguageException extends ParserException {
        public NoSuchLanguageException(String language, Location location) {
            super("Language not supported: " + language, location);
        }
    }

    public static class UnexpectedTokenException extends ParserException {
        public final Token receivedToken;
        public final List<String> expectedTokenTypes;
        public String stateComment;

        public UnexpectedTokenException(Token receivedToken, List<String> expectedTokenTypes, String stateComment) {
            super(getMessage(receivedToken, expectedTokenTypes), getLocation(receivedToken));
            this.receivedToken = receivedToken;
            this.expectedTokenTypes = expectedTokenTypes;
            this.stateComment = stateComment;
        }

        private static String getMessage(Token receivedToken, List<String> expectedTokenTypes) {
            return String.format("expected: %s, got '%s'",
                    StringUtils.join(", ", expectedTokenTypes),
                    receivedToken.getTokenValue().trim());
        }

        private static Location getLocation(Token receivedToken) {
            return receivedToken.location.getColumn() > 1
                    ? receivedToken.location
                    : new Location(receivedToken.location.getLine(), receivedToken.line.indent() + 1);
        }
    }

    public static class UnexpectedEOFException extends ParserException {
        public final String stateComment;
        public final List<String> expectedTokenTypes;

        public UnexpectedEOFException(Token receivedToken, List<String> expectedTokenTypes, String stateComment) {
            super(getMessage(expectedTokenTypes), receivedToken.location);
            this.expectedTokenTypes = expectedTokenTypes;
            this.stateComment = stateComment;
        }

        private static String getMessage(List<String> expectedTokenTypes) {
            return String.format("unexpected end of file, expected: %s",
                    StringUtils.join(", ", expectedTokenTypes));
        }
    }

    public static class CompositeParserException extends ParserException {
        public final List<ParserException> errors;

        public CompositeParserException(List<ParserException> errors) {
            super(getMessage(errors));
            this.errors = Collections.unmodifiableList(errors);
        }

        private static String getMessage(List<ParserException> errors) {
            if (errors == null) throw new NullPointerException("errors");

            StringUtils.ToString<ParserException> exceptionToString = new StringUtils.ToString<ParserException>() {
                @Override
                public String toString(ParserException e) {
                    return e.getMessage();
                }
            };
            return "Parser errors:\n" + StringUtils.join(exceptionToString, "\n", errors);
        }
    }
}
