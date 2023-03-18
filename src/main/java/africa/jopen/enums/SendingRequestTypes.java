package africa.jopen.enums;

public enum SendingRequestTypes {
  None("Not Sending"),
  JSON("Sending JSON Request"),
  XML( "Sending XML Request"),

    YAML("Sending YAML Request"),
    TEXT("Sending Text Request"),
    BINARY("Sending Binary Request");

    private String name;

    private SendingRequestTypes(String theType) {
        this.name = theType;
    }
}
