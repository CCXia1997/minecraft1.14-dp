package net.minecraft.server.dedicated;

import org.apache.logging.log4j.LogManager;
import java.io.OutputStream;
import java.io.InputStream;
import java.util.Properties;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import net.minecraft.SharedConstants;
import java.nio.file.Path;
import org.apache.logging.log4j.Logger;

public class EulaReader
{
    private static final Logger LOGGER;
    private final Path eulaFile;
    private final boolean eulaAgreedTo;
    
    public EulaReader(final Path path) {
        this.eulaFile = path;
        this.eulaAgreedTo = (SharedConstants.isDevelopment || this.checkEulaAgreement());
    }
    
    private boolean checkEulaAgreement() {
        try (final InputStream inputStream1 = Files.newInputStream(this.eulaFile)) {
            final Properties properties3 = new Properties();
            properties3.load(inputStream1);
            return Boolean.parseBoolean(properties3.getProperty("eula", "false"));
        }
        catch (Exception exception1) {
            EulaReader.LOGGER.warn("Failed to load {}", this.eulaFile);
            this.createEulaFile();
            return false;
        }
    }
    
    public boolean isEulaAgreedTo() {
        return this.eulaAgreedTo;
    }
    
    private void createEulaFile() {
        if (SharedConstants.isDevelopment) {
            return;
        }
        try (final OutputStream outputStream1 = Files.newOutputStream(this.eulaFile)) {
            final Properties properties3 = new Properties();
            properties3.setProperty("eula", "false");
            properties3.store(outputStream1, "By changing the setting below to TRUE you are indicating your agreement to our EULA (https://account.mojang.com/documents/minecraft_eula).");
        }
        catch (Exception exception1) {
            EulaReader.LOGGER.warn("Failed to save {}", this.eulaFile, exception1);
        }
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
