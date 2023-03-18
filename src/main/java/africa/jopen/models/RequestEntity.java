package africa.jopen.models;

public record RequestEntity (int id , int navigationID, String url ,String query ,String headers,
                             String auth ,String body ,String bodyType ,String response , String responseHeaders ,
                             String responseCookies,String responseCode,String responseTimeSpent,String timeStamp) {
}
