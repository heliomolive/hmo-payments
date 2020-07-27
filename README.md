# Spring Boot State Machine example project

In this example project we use Spring Boot State Machine to construct an application to process credit card operations (authorization, pre-authorization).

select p.*, pa.*, a.*
from payment p
left join pre_auth pa on pa.payment_id=p.payment_id
left join auth a on a.payment_id=p.payment_id
