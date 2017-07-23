package de.cauchypeano.demo.stoppableintegration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StoppingController {

    @Autowired
    @Qualifier("controlChannel")
    private MessageChannel controlChannel;

    @RequestMapping(value = "/stop", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String stopPollers() {

        controlChannel.send(new GenericMessage<>("@sftpInboundAdapter.stop()"));

        return "stopping...";
    }

    @RequestMapping(value = "/start", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String startPollers() {

        controlChannel.send(new GenericMessage<>("@sftpInboundAdapter.start()"));

        return "started...";
    }

}
