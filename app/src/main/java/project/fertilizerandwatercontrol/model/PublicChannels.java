package project.fertilizerandwatercontrol.model;

/**
 * Created by waron on 4/4/2560.
 */

import java.util.List;

/***
 * Data model of a list of ThingSpeak public Channels.
 *
 * @author Macro Yau
 */
public class PublicChannels {

    private List<Channel> channels;

    /***
     * Get the list of public Channels.
     *
     * @return the Channels
     */
    public List<Channel> getChannels() {
        return channels;
    }

}