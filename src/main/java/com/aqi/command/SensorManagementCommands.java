package com.aqi.command;

import com.aqi.service.SensorNodeService;
import com.aqi.entity.SensorNode;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class SensorManagementCommands {
    private final SensorNodeService sensorNodeService;

    public SensorManagementCommands(SensorNodeService sensorNodeService) {
        this.sensorNodeService = sensorNodeService;
    }

    @ShellMethod(key = "add-sensor", value = "Creates a new sensor node record and outputs the id needed for certificate generation.")
    public String addSensor(
            @ShellOption(help = "A friendly name for the sensor.") String name,
            @ShellOption(help = "The physical location of the sensor.") String location
    ) {
        try {
            SensorNode newNode = sensorNodeService.addNode(name, location);
            String id = newNode.getId();

            return "\n"
                    + "Sensor '" + name + "' added to database!\n"
                    + "--------------------------------------------------------\n"
                    + "DATABASE RECORD CREATED:\n"
                    + "  id (CN): \t\033[1m" + id + "\033[0m\n"
                    + "  Name: \t" + name + "\n"
                    + "  Location: \t" + location + "\n"
                    + "--------------------------------------------------------\n"
                    + "**CRITICAL MANUAL STEPS REQUIRED FOR SECURITY:**\n"
                    + "\n"
                    + "1. GENERATE CERTIFICATE: Manually create a client certificate (.crt) and private key (.key) for this sensor.\n"
                    + "   - The \033[1mCommon Name (CN)\033[0m of the certificate \033[1mMUST\033[0m be set to the id above: \033[1m" + id + "\033[0m\n"
                    + "\n"
                    + "2. UPDATE ACL: Add the following entry to your Mosquitto ACL file and reload the broker:\n"
                    + "   user " + id + "\n"
                    + "   topic write sensors/" + id + "/#\n"
                    + "--------------------------------------------------------\n";

        } catch (Exception e) {
            return "Error adding sensor: " + e.getMessage();
        }
    }

    @ShellMethod(key = "remove-sensor", value = "Deletes a sensor node record and advises on manual security cleanup.")
    public String removeSensor(
            @ShellOption(help = "The unique Id/CN of the sensor to remove.") String id
    ) {
        try {
            sensorNodeService.removeNode(id);

            return "\n"
                    + "Sensor with id '" + id + "' successfully deleted from the database.\n"
                    + "--------------------------------------------------------\n"
                    + "\033[1mCRITICAL MANUAL CLEANUP REQUIRED:\033[0m\n"
                    + "1. \033[1mCERTIFICATES:\033[0m Revoke the sensor's client certificate using your CA tools to permanently invalidate it.\n"
                    + "2. \033[1mMOSQUITTO ACL:\033[0m Manually remove the corresponding ACL entry (user " + id + ") from your Mosquitto ACL file and reload the broker.\n"
                    + "--------------------------------------------------------\n";

        } catch (IllegalArgumentException e) {
            return "Error: " + e.getMessage();
        } catch (Exception e) {
            return "An unexpected error occurred during removal: " + e.getMessage();
        }
    }
}