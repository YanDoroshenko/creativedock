# creativedock
Simple REST + Apache Kafka CRUD application

## REST Interface
### Create group
#### Endpoint:
`/groups`
#### Method
`POST`
#### Request
```
{
   "name" : $new_group_name
}
```
#### Response
```
200 OK // Since we can't be sure that the group was actually created
```
### Add message to a group
#### Endpoint:
`/groups/$group`
#### Method
`PUT`
#### Request
```
{
   "message" : $message_text
}
```
#### Response
```
200 OK // Since we can't be sure that the message was actually added
```
### List messages for a group
#### Endpoint:
`/groups/$group/messages`
#### Method
`GET`
#### Response
##### Messages found:
###### Status
```
200 OK
```
###### Body
```
{
  "name": $group
  "messages": [
    $message1,
    $message2
   ]
}
```
##### Messages not found (group does not exist)
###### Status
```
404 NOT FOUND
```
### Delete group
#### Endpoint:
`/groups/$group`
#### Method
`DELETE`
#### Response
```
200 OK // Since we can't be sure that the group was actually deleted
```
