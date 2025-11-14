package com.aqi.command;

import com.aqi.service.TenantService;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class TenantManagementCommands {
    private final TenantService tenantService;

    public TenantManagementCommands(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @ShellMethod(key = "create-tenant", value = "Creates a new tenant and outputs its secret token.")
    public String createTenantCommand(
            @ShellOption(help = "The unique name for the new tenant.")
            String name) {

        try {
            String secretToken = tenantService.createNewTenant(name);

            return "\n"
                    + "Tenant '" + name + "' successfully created.\n"
                    + "--------------------------------------------------------\n"
                    + "SECRET TOKEN (Treat this like a password):\n"
                    + "   " + secretToken + "\n"
                    + "--------------------------------------------------------\n"
                    + "Do not lose this token; it cannot be retrieved later.\n";

        } catch (Exception e) {
            return "Error creating tenant '" + name + "': " + e.getMessage();
        }
    }
}