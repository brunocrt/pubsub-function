## Simple Google Cloud Message Publisher Function using Java 11
This is a simple demo using GCP Serverless Function that publisher a message to a PubSub Topic

## Before you test
Set the environment variable with your project ID
`set GOOGLE_APPLICATION_CREDENTIALS=service-account.json`
`set GOOGLE_CLOUD_PROJECT=pacific-diode-316600`
`set PUBSUB_TOPIC_NAME=projects/pacific-diode-316600/topics/mytopic`

### To Test it localy run
`mvn function:run`

### To deploy it to GCP
`gcloud functions deploy PublisherFunction --entry-point me.brunocrt.gcp.functions.PublisherFunction --runtime java11 --trigger-http --memory 128MB --allow-unauthenticated`

### To Check the function details
`gcloud functions describe PublisherFunction`

### To check the logs
`gcloud functions logs read PublisherFunction`
