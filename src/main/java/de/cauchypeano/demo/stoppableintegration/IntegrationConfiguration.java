package de.cauchypeano.demo.stoppableintegration;

import com.jcraft.jsch.ChannelSftp;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.integration.dsl.core.Pollers;
import org.springframework.integration.dsl.sftp.Sftp;
import org.springframework.integration.dsl.sftp.SftpInboundChannelAdapterSpec;
import org.springframework.integration.file.remote.session.CachingSessionFactory;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Configuration
@EnableIntegration
public class IntegrationConfiguration {

    @Bean
    public DefaultSftpSessionFactory defaultSftpSessionFactory() {
        DefaultSftpSessionFactory sessionFactory = new DefaultSftpSessionFactory();
        sessionFactory.setHost("localhost");
        sessionFactory.setPort(22);

        sessionFactory.setUser("user");

        sessionFactory.setEnableDaemonThread(false);

        sessionFactory.setTimeout(0);

        sessionFactory.setPassword("password");

        sessionFactory.setAllowUnknownKeys(true);

        return sessionFactory;
    }

    @Bean
    public CachingSessionFactory<ChannelSftp.LsEntry> sftpSessionFactory(
            DefaultSftpSessionFactory defaultSftpSessionFactory) {
        return new CachingSessionFactory<>(defaultSftpSessionFactory);
    }

    @Bean
    public IntegrationFlow fileFlow() throws IOException {

        Path tempDirectory = Files.createTempDirectory("deleteme");

        SftpInboundChannelAdapterSpec spec = Sftp.inboundAdapter(defaultSftpSessionFactory())
                .patternFilter("*.xml")
                .remoteDirectory("/")
                .autoCreateLocalDirectory(true)
                .deleteRemoteFiles(false)
                .localDirectory(tempDirectory.toFile())
                .temporaryFileSuffix(".lol");

        IntegrationFlow integrationFlow = IntegrationFlows
                .from(spec, e -> e.id("sftpInboundAdapter").autoStartup(true).poller(sftpPoller()))
                .channel(receiveChannel())
                .handle(myMessageHandler())
                .get();
        return integrationFlow;
    }

    @Bean
    public IntegrationFlow controllerHaha() {
        return IntegrationFlows.from(controlChannel())
                .controlBus()
                .get();
    }

    private MessageHandler myMessageHandler() {
        return message -> System.out.println("Got message " + message);
    }

    @Bean
    public MessageChannel receiveChannel() {
        return MessageChannels.direct().get();
    }

    @Bean
    public MessageChannel controlChannel() {
        return MessageChannels.direct().get();
    }

    @Bean
    public PollerMetadata sftpPoller() {
        return Pollers.fixedRate(5000)
                .maxMessagesPerPoll(10)
                .get();
    }

}
