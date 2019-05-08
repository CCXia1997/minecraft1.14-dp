package net.minecraft.client.audio;

import org.apache.logging.log4j.LogManager;
import javax.sound.sampled.AudioFormat;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.AL10;
import org.apache.logging.log4j.Logger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class AlUtil
{
    private static final Logger LOGGER;
    
    private static String getErrorMessage(final int errorCode) {
        switch (errorCode) {
            case 40961: {
                return "Invalid name parameter.";
            }
            case 40962: {
                return "Invalid enumerated parameter value.";
            }
            case 40963: {
                return "Invalid parameter parameter value.";
            }
            case 40964: {
                return "Invalid operation.";
            }
            case 40965: {
                return "Unable to allocate memory.";
            }
            default: {
                return "An unrecognized error occurred.";
            }
        }
    }
    
    static boolean checkErrors(final String sectionName) {
        final int integer2 = AL10.alGetError();
        if (integer2 != 0) {
            AlUtil.LOGGER.error("{}: {}", sectionName, getErrorMessage(integer2));
            return true;
        }
        return false;
    }
    
    private static String getAlcErrorMessage(final int errorCode) {
        switch (errorCode) {
            case 40961: {
                return "Invalid device.";
            }
            case 40962: {
                return "Invalid context.";
            }
            case 40964: {
                return "Invalid value.";
            }
            case 40963: {
                return "Illegal enum.";
            }
            case 40965: {
                return "Unable to allocate memory.";
            }
            default: {
                return "An unrecognized error occurred.";
            }
        }
    }
    
    static boolean checkAlcErrors(final long deviceHandle, final String sectionName) {
        final int integer4 = ALC10.alcGetError(deviceHandle);
        if (integer4 != 0) {
            AlUtil.LOGGER.error("{}{}: {}", sectionName, deviceHandle, getAlcErrorMessage(integer4));
            return true;
        }
        return false;
    }
    
    static int getFormatId(final AudioFormat format) {
        final AudioFormat.Encoding encoding2 = format.getEncoding();
        final int integer3 = format.getChannels();
        final int integer4 = format.getSampleSizeInBits();
        if (encoding2.equals(AudioFormat.Encoding.PCM_UNSIGNED) || encoding2.equals(AudioFormat.Encoding.PCM_SIGNED)) {
            if (integer3 == 1) {
                if (integer4 == 8) {
                    return 4352;
                }
                if (integer4 == 16) {
                    return 4353;
                }
            }
            else if (integer3 == 2) {
                if (integer4 == 8) {
                    return 4354;
                }
                if (integer4 == 16) {
                    return 4355;
                }
            }
        }
        throw new IllegalArgumentException("Invalid audio format: " + format);
    }
    
    static {
        LOGGER = LogManager.getLogger();
    }
}
