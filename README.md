# example of stoppable poller

This small example shows how to stop poller using Spring integration with Java DSL.

To reproduce it you need to start locally SFTP. I would recommend Rebex Tiny Sftp server.

To stop poller write:
`curl -X POST localhost:8080/stop`

To start it: `curl -X POST localhost:8080/start`

