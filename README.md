# Spring Boot State Machine example project

This example project uses Spring State Machine to construct an application that processes credit card operations (authorization, pre-authorization).

Sorry for not including tests. I developed this project while I was studying how to use state machines, some strategies changed during the project execution. So it was easier to me testing by running the application than creating unit tests. 

**Asynchronous processing**

Credit card operations require interaction with an acquirer service, which will most often call external services.

This project uses an strategy of asynchronous processing to handle such operations. Pre-authorization, authorization and cancel operations will first change payment to a requested state (for instance, `PRE_AUTH_REQUESTED`) and then process asynchronously the operation. 

Asynchronous processing is accomplished with Spring State Machine's support. In fact, when we use a state action the State Machine will trigger a thread to execute this action.

Consequently, payment status returned by the endpoint may not be up to date. The application endpoint will return while our State Machine is still working.

To get an up to date state, we can call the endpoint `GET /payment-order/v1/payment/{paymentId}`. 
 