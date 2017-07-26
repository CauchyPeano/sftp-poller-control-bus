package de.cauchypeano.demo.stoppableintegration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.integration.endpoint.SourcePollingChannelAdapter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@DependsOn("fileFlow")
public class StoppingController {

    @Autowired
    @Qualifier("controlChannel")
    private MessageChannel controlChannel;

    @Autowired
    private AbstractApplicationContext abstractApplicationContext;

    @Autowired
    @Qualifier("sftpInboundAdapter") SourcePollingChannelAdapter sftpInboundAdapter;

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

    @RequestMapping(value = "/poller", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String getIsRunning() {

        return "status: " + sftpInboundAdapter.isRunning();
    }

}
