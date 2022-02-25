package academy.mindswap.messages;

public abstract class Messages {

        public static final String DEFAULT_NAME = "CLIENT-";
        public static final String CLIENT_ENTERED_CHAT = " entered the chat.";
        public static final String NO_SUCH_COMMAND = "⚠️ Invalid command!";
        public static final String COMMANDS_LIST = """
            List of available commands:
            /list -> gets you the list of connected clients
            /whisper <username> <message> -> lets you whisper a message to a single connected client
            /quit -> exits the server""";
        public static final String CLIENT_DISCONNECTED = " left the chat.";
        public static final String WHISPER_INSTRUCTIONS = "Invalid whisper use. Correct use: '/whisper <username> <message>";
        public static final String NO_SUCH_CLIENT = "The client you want to whisper to doesn't exists.";
        public static final String WHISPER = "(whisper)";
        public static final String WELCOME = "Welcome to MindSwap's chat!";
        public static final String CLIENT_ERROR = "Something went wrong with this client's connection. Error: ";
}
