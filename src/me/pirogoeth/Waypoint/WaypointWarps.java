package me.pirogoeth.Waypoint;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.util.config.Configuration;

public class WaypointWarps {
    public static Waypoint plugin;
    public static Configuration config;
    public static Logger log = Logger.getLogger("Minecraft");
    public List<String> groups;
    public WaypointWarps (Waypoint instance)
    {
        plugin = instance;
        config = plugin.config;
    }
    public void LoadGroups ()
    {
        groups = config.getStringList("warp.groups", null);
        Iterator<String> i = groups.iterator();
        StringBuffer s = new StringBuffer(i.next());
        while (i.hasNext()) { s.append(", ").append(i.next()); }
        log.info(String.format("[Waypoint] Warps: loaded permission groups: %s", s.toString()));
        return;
    }
    public String WarpNode (String warpname, String subnode)
    {
        String a = "warps." + warpname + "." + subnode;
        return a;
    }
    public String WarpBase (String warpname)
    {
        String a = "warps." + warpname;
        return a;
    }
    public boolean checkperms (Player p, String pnode)
    {
        String permission = String.format("waypoint.warp.access.%s", pnode);
        return plugin.permissions.has(p, permission);
    }
    public void CreateWarp (Player p, String warpname)
    {
        Location l = p.getLocation();
        double x = l.getX();
        double y = l.getY();
        double z = l.getZ();
        String worldname = p.getWorld().getName().toString();
        String owner = p.getName().toString();
        config.setProperty(WarpNode(warpname, "coord.X"), x);
        config.setProperty(WarpNode(warpname, "coord.Y"), y);
        config.setProperty(WarpNode(warpname, "coord.Z"), z);
        config.setProperty(WarpNode(warpname, "world"), worldname);
        config.setProperty(WarpNode(warpname, "owner"), owner);
        config.setProperty(WarpNode(warpname, "permission"), "general");
        config.save();
        return;
    }
    public boolean DeleteWarp (String warpname)
    {
        if (config.getProperty(WarpNode(warpname, "world")) == null)
        {
            return false;
        }
        config.removeProperty(WarpBase(warpname));
        config.save();
        return true;
    }
    public boolean CheckGroup (String group)
    {
        return groups.contains(group);
    }
    public boolean SetWarpProp (String warpname, String key, String value)
    {
        if (config.getProperty(WarpNode(warpname, "world")) == null)
        {
            return false;
        }
        if (key.equalsIgnoreCase("permission") && CheckGroup(value) || key.equalsIgnoreCase("owner"))
        {
            config.setProperty(WarpNode(warpname, key), value);
            config.save();
            return true;
        }
        else if (key.equalsIgnoreCase("permission") && !CheckGroup(value))
        {
            return false;
        }
    return false;
    }
    public boolean PlayerToWarp (Player p, String warpname)
    {
        if (config.getProperty(WarpNode(warpname, "world")) == null)
        {
            p.sendMessage(ChatColor.RED + "[Waypoint] Warp " + warpname + " does not exist.");
            return false;
        }
        String permission = (String) config.getProperty(WarpNode(warpname, "permission"));
        if (checkperms(p, permission) == false)
        {
            p.sendMessage(ChatColor.RED + "[Waypoint] You do not have permissions to access this warp.");
            return false;
        }
        double x = (Double) config.getProperty(WarpNode(warpname, "coord.X"));
        double y = (Double) config.getProperty(WarpNode(warpname, "coord.Y"));
        double z = (Double) config.getProperty(WarpNode(warpname, "coord.Z"));
        String worldname = (String) config.getProperty(WarpNode(warpname, "world"));
        World w = plugin.getServer().getWorld(worldname);
        Location l = new Location(w, x, y, z);
        p.teleport(l);
        p.sendMessage(ChatColor.AQUA + "Welcome to " + warpname + ", " + p.getName().toString());
        return true;
    }
}