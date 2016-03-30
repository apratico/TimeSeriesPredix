# Time Series Predix
Welcome to Time Series Mock Application. This is a Java Application for calling the Predix Time Series API to post data and with Jetty embedded on port 5000 to expose swagger UI for use our REST API and a web interface for governance. It has built in mechanism to perform authenticated calls to Time Series API and post data for a list sensors. Each sensor have some parameters to configure data to generate (normal range, mesures over range, sample time, etc)

# Build it

    From the command line, go the the project directory.
    Run as

  mvn clean package
