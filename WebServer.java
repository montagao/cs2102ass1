/*
Name: Monta Zimu Gao
Student number: A0178701A
Is this a group submission no 

If it is a group submission:
Name of 2nd group member: THE_OTHER_NAME_HERE_PLEASE
Student number of 2nd group member: THE_OTHER_NO

*/


import java.net.*; // Defines Socket and ServerSocket Classes
import java.nio.ByteBuffer;
import java.nio.file.*;
import java.io.*;
import java.util.regex.*; // defines some regex utils (useful for parsing)
          
public class WebServer {
    static int STRING_BUFFER_SIZE = 1024;

    public static void main(String[] args) throws IOException{
        // dummy value that is overwritten below
        int port = 8080;
        try {
          port = Integer.parseInt(args[0]);
        } catch (Exception e) {
          System.out.println("Usage: java WebServer <port> ");
          System.exit(0);
        }

        WebServer serverInstance = new WebServer();
        serverInstance.start(port);
    }

    private void start(int port) throws IOException  { // replace with catch statements
      System.out.println("Starting server on port " + port);
      // START_HERE
      ServerSocket sSocket = new ServerSocket(port);

      while (true) {  // keep server alive
        // "Poll" for socket connection (accept all connection requests)
        System.out.println("Waiting to accept socket connection...");
        Socket connectionSocket = sSocket.accept();
        System.out.println("Socket connection established. Handling socket connection ... ");
        // Boolean isHttp10 =
        this.handleClientSocket(connectionSocket); 
        System.out.println("Finished handling socket connection.");
        // if( isHttp10 )

      }
      // Please DO NOT copy from the Internet (or anywhere else)
      // Instead, if you see nice code somewhere try to understand it.
      //
      // After understanding the code, put it away, do not look at it,
      // and write your own code.
      // Subsequent exercises will build on the knowledge that
      // you gain during this exercise. Possibly also the exam.
      //
      // We will check for plagiarism. Please be extra careful and
      // do not share solutions with your friends.
      //
      // Good practices include
      // (1) Discussion of general approaches to solve the problem
      //     excluding detailed design discussions and code reviews.
      // (2) Hints about which classes to use
      // (3) High level UML diagrams
      //
      // Bad practices include (but are not limited to)
      // (1) Passing your solution to your friends
      // (2) Uploading your solution to the Internet including
      //     public repositories
      // (3) Passing almost complete skeleton codes to your friends
      // (4) Coding the solution for your friend
      // (5) Sharing the screen with a friend during coding
      // (6) Sharing notes
      //
      // If you want to solve this assignment in a group,
      // you are free to do so, but declare it as group work above.
      
      


      // NEEDS IMPLEMENTATION
      // You have to understand how sockets work and how to program
      // them in Java.
      // A good starting point is the socket tutorial from Oracle
      // http://docs.oracle.com/javase/tutorial/networking/sockets/
      // But there are a billion other resources on the Internet.
      //
      // Hints
      // 1. You should set up the socket(s) and then call handleClientSocket.
      
    }




    /**
     * Handles requests sent by a client
     * @param  client Socket that handles the client connection
     */
    private void handleClientSocket(Socket client) throws IOException {
      // NEEDS IMPLEMENTATION
      // This function is supposed to handle the request
      // Things to do:
      // (1) Read the request from the socket 
      // (2) Parse the request and set variables of 
      //     the HttpRequest class (at the end of the file!)
      // (3) Form a response using formHttpResponse.
      // (4) Send a response using sendHttpResponse.
      //
      // A BufferedReader might be useful here, but you can also
      // solve this in many other ways.


      // error case for non http requests need to be handled?

      InputStreamReader iStreamReader = new InputStreamReader(client.getInputStream());
      BufferedReader bReader = new BufferedReader(iStreamReader);

      // initialize a new httpRequest 
      HttpRequest httpRequest = new HttpRequest(bReader);

      if( httpRequest.getIsHttp10() ){
        byte[] response = formHttpResponse(httpRequest);
        System.out.println("Finished forming http response");
        sendHttpResponse( client, response);
        client.close();
      } else {
        long timeStarted = System.currentTimeMillis();
        do {
          // TODO: send asynchronously with new thread.
          // System.out.println("Here");
          if( httpRequest.getIsValid() ){
            System.out.println("Valid request for " + httpRequest.getFilePath() + " found.");
            byte[] response = formHttpResponse(httpRequest);
            if( response == null ){
              System.out.println( "Something went wrong, either the request requested an "
                + "invalid resource or we couldn't form the response" );
              break;
            }
            System.out.println("Finished forming http response");
            sendHttpResponse( client, response);
          } else {
            // System.out.println("Invalid request/ no request.");
          }
        // bad polling
        } while( (httpRequest = new HttpRequest(bReader)).getIsValid() ||
         System.currentTimeMillis() < (timeStarted + 2000)) ;

        client.close();
      }
    }

    /**
     * Sends a response back to the client
     * @param  client Socket that handles the client connection
     * @param  response the response that should be send to the client
     */
    private void sendHttpResponse(Socket client, byte[] response) throws IOException {
      // get output stream of client connection's socket
      System.out.println("Sending httpResponse w/ bytes:\n" + new String(response) );
      OutputStream clientSocketOutputStream = client.getOutputStream();
      int bytesRead = response.length;
      clientSocketOutputStream.write( response );
      System.out.println( "Wrote " + bytesRead + " bytes to client connetion output socket." );
    }

    /**
     * Form a response to an HttpRequest
     * @param  request the HTTP request
     * @return a byte[] that contains the data that should be send to the client
     */
    private byte[] formHttpResponse(HttpRequest request) throws IOException, FileNotFoundException {
      // NEEDS IMPLEMENTATION
      // Make sure you follow the (modified) HTTP specification
      // in the assignment regarding header fields and newlines
      // You might want to use the concatenate method,
      // but you do not have to.
      // If you want to you can use a StringBuilder here
      // but it is possible to solve this in multiple different ways.
      System.out.println("Forming Http response for request at " + request.getFilePath() );

      try {
        // buffer for file specified in request.filePath
        //  ======================= IO handling ======================= //
        String filePath = request.getFilePath();
        byte[] encoded = Files.readAllBytes(Paths.get(filePath));
        int contentLength = encoded.length;

        // Might be bad practice because of encoding issues
        String fileContent = new String(encoded);


        // ======================= Header Buillding ======================= //
        String httpVersion = ( request.getIsHttp10() ? "1.0" : "1.1" );
        // Build the appropriate HTTP Status Line (1.1/1.0)
        StringBuilder rStringBuilder = new StringBuilder(STRING_BUFFER_SIZE);

        String statusLine = "HTTP/" + httpVersion + " " + "200 OK" + "\r\n";
        rStringBuilder.append( statusLine );

        String entityHeader = "Content-Length:" + " " + contentLength + "\r\n";
        rStringBuilder.append( entityHeader );

        rStringBuilder.append( "\r\n" );

        // something feels iffy about this..
        return concatenate( rStringBuilder.toString().getBytes(), encoded ); 
      } catch (IOException E ){
        System.out.println("Bad request: 404 file not found -- too lazy to implement properly");
        return null;

      }

    }
    

    /**
     * Concatenates 2 byte[] into a single byte[]
     * This is a function provided for your convenience.
     * @param  buffer1 a byte array
     * @param  buffer2 another byte array
     * @return concatenation of the 2 buffers
     */
    private byte[] concatenate(byte[] buffer1, byte[] buffer2) {
        byte[] returnBuffer = new byte[buffer1.length + buffer2.length];
        System.arraycopy(buffer1, 0, returnBuffer, 0, buffer1.length);
        System.arraycopy(buffer2, 0, returnBuffer, buffer1.length, buffer2.length);
        return returnBuffer;
    }



}



class HttpRequest {
    // NEEDS IMPLEMENTATION
    // This class should represent a HTTP request.
    // Feel free to add more attributes if needed.
    private String filePath;
    private Boolean isHttp10;
    private Boolean isValid;
    private static Pattern rLinePattern =
      Pattern.compile("\\s*GET\\s+\\/(.+?)\\s+HTTP\\/(1\\.1|1\\.0)$");
    // host not necessary since we use an absolute path *relative* to 
    // the cwd of the Web Server.

    public HttpRequest(BufferedReader bReader) throws IOException {
      this.parseRequest(bReader);
    }

    String getFilePath() {
        return filePath;
    }
    Boolean getIsHttp10() {
        return isHttp10;
    }
    Boolean getIsValid() {
        return isValid;
    }

    /**
     * takes a bufferedreader as an http request and 
     * parses  it. Updates buffer so it's ready for the next request 
     *@param buffered Reader
     *
     */
    private void parseRequest( BufferedReader bReader ) throws IOException {
      String requestLine = bReader.readLine();
      if( requestLine == null){
        this.isValid = false;
        return;
      }

      Matcher rLineMatcher = rLinePattern.matcher(requestLine);
      // should find 3 matching groups for valid HTTP
      
      if( rLineMatcher.matches() && rLineMatcher.groupCount() == 2 ){
        this.filePath = rLineMatcher.group(1);
        this.isHttp10 = ( rLineMatcher.group(2).equals("1.0") );

        System.out.println( requestLine );
        this.readBufferToEnd( bReader );
        this.isValid = true;
      } else {
        System.out.println( "Invalid Request Headers" );
        this.readBufferToEnd( bReader );
        this.isValid = false;
      }
    }

    private void readBufferToEnd( BufferedReader bReader ) throws IOException {
      if( this.isHttp10 ){
        System.out.println("HTTP/1.0 Request - Ending socket Connection...");
        // discard the rest of the request
        // actually this isn't even necessary since we create a new connection
      } else {
        // only discard until the beginning of next request.
        System.out.println("HTTP/1.1 Request - Preparing to read next request from socket connection");
        String s = "";
        // Matcher rLineMatcher = rLinePattern.matcher(s);
        while ( !(( s = bReader.readLine() ).trim().equals("")) /* && !rLineMatcher.matches() */ );
        System.out.println("HTTP/1.1 Request - Buffer ready for next request!");
      }
    }
    // NEEDS IMPLEMENTATION
    // If you add more private variables, add your getter methods here
}