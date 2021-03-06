package me.pirogoeth.Waypoint.Core;

import java.lang.Float;

import java.util.Map;
import java.util.List;
import java.util.Iterator;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.Location;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

import me.pirogoeth.Waypoint.Waypoint;
import me.pirogoeth.Waypoint.Util.Permission;
import me.pirogoeth.Waypoint.Util.Config;

public class Parser {
    public static Waypoint plugin;
    public static Config c;
    public static Configuration main;
    public static Configuration users;
    public static Configuration home;
    public static Configuration warp;
    public static Permission permissions;
    public static Logger log = Logger.getLogger("Minecraft");
    public Parser (Waypoint instance)
    {
    	plugin = instance;
    	c = plugin.config;
    	users = c.getUsers();
        permissions = plugin.permissions;
    	home = c.getHome();
    	main = c.getMain();
    	warp = c.getWarp();
    }
    public static String BaseNodeChomp(Player p, String node)
    {
        String a = "users." + p.getName().toString() + "." + node;
        return a;
    }
    public static String UserNodeChomp(Player p, String targetname, String sub)
    {
        String a = "users." + p.getName().toString() + "." + targetname + "." + sub;
        return a;
    }
    public static String HomeNodeChomp(Player p, World world, String sub)
    {
        String a = "home." + p.getName().toString() + "." + world.getName().toString() + "." + sub;
        return a;
    }
    public static String InviteNodeChomp(Player p, String node)
    {
        String a = "invites." + p.getName().toString() + "." + node;
        return a;
    }
    public static void TransferNode(String path, ConfigurationNode node)
    {
        Map<String, Object> a = node.getAll();
        for (Map.Entry<String, Object> entry : a.entrySet())
        {
            users.setProperty(path + "." + entry.getKey(), entry.getValue());
            // debug
            //  log.info("Key: " + entry.getKey() + " Value: " + (String)entry.getValue().toString());
        }
        return;
    }
    public static boolean CheckPointExists(Player p, String point)
    {
        String a = "users." + p.getName().toString() + "." + point;
        if (users.getProperty(a + ".coord") == null)
        {
            return false;
        } else if (users.getProperty(a + ".coord") != null)
        {
            return true;
        } else
        {
            return false;
        }
    }
    public static boolean CommandParser(Player player, String command, String[] args) {
    if (command.equalsIgnoreCase("wp") || command.equalsIgnoreCase("waypoint"))
   	{
        String subc = "";
        try {
 	        subc = args[0];
        }
        catch (java.lang.ArrayIndexOutOfBoundsException e) {
            player.sendMessage("Usage: /wp <add|del|tp|list|help> [name]");
            return true;
        };
  	    subc = subc.toLowerCase().toString();
        String arg = null;
        try {
            arg = args[1];
        }
        catch (java.lang.ArrayIndexOutOfBoundsException e) {
            arg = null;
  	    };
        if (subc.equalsIgnoreCase("add"))
        {
         	if (arg == null) { player.sendMessage("Usage: /wp <add|del|tp|list|help> [name]"); }
            if (!permissions.has(player, "waypoint.basic.add")) {
                player.sendMessage(ChatColor.BLUE + "You do not have the permissions to use this command."); 
                return true;
            }
           	if (users.getProperty(UserNodeChomp(player, arg, "world")) != null)
       	    {
       		    player.sendMessage("[Waypoint] Point '" + arg + "' already exists!");
       		    return true;
           	}
           	// position
         	users.setProperty(UserNodeChomp(player, arg, "coord.X"), player.getLocation().getX());
           	users.setProperty(UserNodeChomp(player, arg, "coord.Y"), player.getLocation().getY());
           	users.setProperty(UserNodeChomp(player, arg, "coord.Z"), player.getLocation().getZ());
            users.setProperty(UserNodeChomp(player, arg, "coord.yaw"), (double)player.getLocation().getYaw());
            users.setProperty(UserNodeChomp(player, arg, "coord.pitch"), (double)player.getLocation().getPitch());
           	// eye direction
           	// not needed anymore: users.setProperty(UserNodeChomp(player, arg, "coord.pitch"), player.getLocation().getPitch());
           	//  users.setProperty(UserNodeChomp(player, arg, "coord.yaw"), player.getLocation().getYaw());
           	// number tests
           	//  log.info("" + String.format("Pitch: %s, Yaw: %s", player.getLocation().getPitch(), player.getLocation().getYaw()));
           	//  double pa = new Double(player.getLocation().getPitch());
           	//  double ya = new Double(player.getLocation().getYaw());
           	//  float pb = new Float(player.getLocation().getPitch());
           	//  float yb = new Float(player.getLocation().getYaw());
           	//  log.info("" + String.format("DPitch: %s, DYaw: %s", pa, ya));
           	//  log.info("" + String.format("FPitch: %s, FYaw: %s", pb, yb));
           	// current world
           	users.setProperty(UserNodeChomp(player, arg, "world"), player.getLocation().getWorld().getName().toString());
           	users.save();
           	player.sendMessage(ChatColor.GREEN + "[Waypoint] Set point '" + arg + "' in world '" + player.getLocation().getWorld().getName().toString() + "'.");
           	return true;
        }
        else if (subc.equalsIgnoreCase("del") || subc.equalsIgnoreCase("delete") || subc.equalsIgnoreCase("remove"))
        {
            if (arg == null) { player.sendMessage("Usage: /wp <add|del|tp|list|help> [name]"); }
	        if (!permissions.has(player, "waypoint.basic.delete")) { 
	            player.sendMessage(ChatColor.BLUE + "You do not have the permissions to use this command."); 
	            return true;
	        }
            if (users.getProperty(UserNodeChomp(player, arg, "world")) == null)
            {
                player.sendMessage(ChatColor.RED + "[Waypoint] Point '" + arg + "' does not exist!");
                return true;
            }
            users.removeProperty("users." + player.getName().toString() + "." + arg);
            player.sendMessage("[Waypoint] Point '" + arg + "' has been deleted.");
            users.save();
            return true;
        }
        else if (subc.equalsIgnoreCase("tp") || subc.equalsIgnoreCase("teleport"))
        {
          	if (arg == null) { player.sendMessage(ChatColor.AQUA + "Usage: /wp <add|del|tp|list|help> [name]"); }
	        if (!permissions.has(player, "waypoint.basic.teleport")) { 
	            player.sendMessage(ChatColor.BLUE + "You do not have the permissions to use this command.");
	            return true;
	        }
          	if (users.getProperty(UserNodeChomp(player, arg, "world")) == null)
           	{
           		player.sendMessage(ChatColor.RED + "[Waypoint] Point '" + arg + "' does not exist!");
           		return true;
           	}
           	World w = plugin.getServer().getWorld(users.getProperty(UserNodeChomp(player, arg, "world")).toString());
           	double x = (Double) users.getProperty(UserNodeChomp(player, arg, "coord.X"));
            double y = (Double) users.getProperty(UserNodeChomp(player, arg, "coord.Y"));
            double z = (Double) users.getProperty(UserNodeChomp(player, arg, "coord.Z"));
            double yaw = (Double) users.getProperty(UserNodeChomp(player, arg, "coord.yaw"));
            double pitch = (Double) users.getProperty(UserNodeChomp(player, arg, "coord.pitch"));
    	  	// no longer needed: final float p = new Float(users.getString(UserNodeChomp(player, arg, "coord.pitch")));
    	    //  final float p = new Float("1");
    	    //  float yw = new Float(users.getString(UserNodeChomp(player, arg, "coord.yaw")));
    	    Location l = new Location(w, x, y, z, (float)yaw, (float)pitch);
            boolean su = player.teleport(l);
            if (su == true)
            {
               	player.sendMessage(ChatColor.AQUA + "[Waypoint] Successfully teleported to '" + arg + "'.");
              	return true;
            }
            else if (su == false)
            {
               	player.sendMessage(ChatColor.RED + "[Waypoint] Error while teleporting to '" + arg + "'.");
               	return true;
            }
            return true;
        }
        else if (subc.equalsIgnoreCase("invite"))
        {
       	    if (arg == null) { player.sendMessage(ChatColor.AQUA + "Usage: /wp <add|del|tp|list|invite|help> [name]"); }
	        if (!permissions.has(player, "waypoint.basic.invite")) { 
	            player.sendMessage(ChatColor.BLUE + "You do not have the permissions to use this command.");
	            return true;
	        }
	        String point;
            try {
                point = (String) args[2];
            }
            catch (ArrayIndexOutOfBoundsException e) {
                player.sendMessage(ChatColor.RED + "Usage: /wp invite <playername> <point>");
                return true;
            }
            Player p = plugin.getServer().getPlayer((String)arg);
            if (p == null)
            {
                player.sendMessage(ChatColor.RED + "[Waypoint] Player " + arg + " is offline. Please resend the invitation when he/she is online.");
                return true;
            }
            if (CheckPointExists(player, point) == false)
            {
                player.sendMessage(ChatColor.RED + "You do not own a point named '" + point + "'.");
                return true;
            }
            ConfigurationNode node = users.getNode(BaseNodeChomp(player, point));
            TransferNode(InviteNodeChomp(p, point), node);
            p.sendMessage(ChatColor.AQUA + "[Waypoint] Player " + player.getName().toString() + " has invited you to use their waypoint '" + point + "'.");
            p.sendMessage(ChatColor.GREEN + "Type /wp accept " + point + " to accept their invite.");
            p.sendMessage(ChatColor.GREEN + "Or type /wp decline " + point + " to decline the invite.");
            player.sendMessage(ChatColor.AQUA + "[Waypoint] Sent invite to " + p.getName().toString() + ".");
            users.save();
            return true;
	    }
	    else if (subc.equalsIgnoreCase("accept"))
	    {
           	if (arg == null) { player.sendMessage(ChatColor.AQUA + "Usage: /wp <add|del|tp|list|invite|help> [name]"); }
	        if (!permissions.has(player, "waypoint.basic.invite.accept")) {
	            player.sendMessage(ChatColor.BLUE + "You do not have the permissions to use this command.");
	            return true;
	        }
	        if (users.getProperty(InviteNodeChomp(player, arg)) == null)
	        {
	            player.sendMessage(ChatColor.RED + "[Waypoint] You have no point invite by that name.");
	            return true;
	        }
	        ConfigurationNode n = users.getNode(InviteNodeChomp(player, arg));
	        users.removeProperty(InviteNodeChomp(player, arg));
	        TransferNode(BaseNodeChomp(player, arg), n);
            player.sendMessage(ChatColor.AQUA + "[Waypoint] The point '" + arg + "' is now available in your collection.");
	        users.save();
	    }
	    else if (subc.equalsIgnoreCase("decline"))
	    {
           	if (arg == null) { player.sendMessage(ChatColor.AQUA + "Usage: /wp <add|del|tp|list|invite|help> [name]"); }
            if (!permissions.has(player, "waypoint.basic.invite.decline")) { 
	            player.sendMessage(ChatColor.BLUE + "You do not have the permissions to use this command.");
	            return true;
	        }
	        if (users.getProperty(InviteNodeChomp(player, arg)) == null)
	        {
	            player.sendMessage(ChatColor.RED + "[Waypoint] You have no point invite by that name.");
	            return true;
	        }
	        users.removeProperty(InviteNodeChomp(player, arg));
	        player.sendMessage(ChatColor.AQUA + "[Waypoint] The point invitation '" + arg + "' has been declined.");
	        users.save();
	    }
        else if (subc.equalsIgnoreCase("list"))
        {
	        if (!permissions.has(player, "waypoint.basic.list")) {
	            player.sendMessage(ChatColor.BLUE + "You do not have the permissions to use this command.");
                return true;
	        }
	        if (users.getProperty("users." + player.getName().toString()) == null)
	        {
	            player.sendMessage(ChatColor.RED + "[Waypoint] You do not have any points to list.");
	            return true;
	        }
           	player.sendMessage(ChatColor.YELLOW + "====[Waypoint] Point list:====");
           	Map<String, ConfigurationNode> a = users.getNodes("users." + player.getName());
           	if (a.size() == 0)
           	{
           	    player.sendMessage(ChatColor.AQUA + " - No points have been created.");
           	    return true;
           	}
           	for (Map.Entry<String, ConfigurationNode> entry : a.entrySet())
           	{
                player.sendMessage(ChatColor.GREEN + " - " + entry.getKey());
           	}
           	return true;
        }
        else if (subc.equalsIgnoreCase("test"))
        {
	        if (!permissions.has(player, "waypoint.debug.config_node_test")) {
                return true;
	        }
            // testing node crap
         	double x = player.getLocation().getX();
            double y = player.getLocation().getY();
            double z = player.getLocation().getZ();
            users.setProperty(UserNodeChomp(player, "testnode", "coord.X"), x);
            users.setProperty(UserNodeChomp(player, "testnode", "coord.Y"), y);
            users.setProperty(UserNodeChomp(player, "testnode", "coord.Z"), z);
            users.save();
            ConfigurationNode n = users.getNode(UserNodeChomp(player, "testnode", "coord"));
            TransferNode(UserNodeChomp(player, "testnode", "coord"), n);
            users.save();
            users.removeProperty(BaseNodeChomp(player, "testnode"));
            users.save();
            log.info("Configuration node nesting test successful.");
            return true;
        }
        else if (subc.equalsIgnoreCase("help"))
        {
           	player.sendMessage(ChatColor.BLUE + "Waypoint, version " + plugin.getDescription().getVersion());
           	player.sendMessage(ChatColor.GREEN + "/wp:");
           	player.sendMessage(ChatColor.RED + "   add - add a waypoint to your list.");
           	player.sendMessage(ChatColor.RED + "   del - remove a waypoint from your list.");
           	player.sendMessage(ChatColor.RED + "   tp - teleport to a waypoint in your list.");
           	player.sendMessage(ChatColor.RED + "   list - list the waypoints in your list.");
           	player.sendMessage(ChatColor.RED + "   help - this message.");
           	return true;
        }
        else
        {
           	player.sendMessage("Usage: /wp <add|del|tp|list|help> [name]");
           	return true;
        }
   	    // dude i have no fucking idea where this bracket is supposed
   	    // to actually go.
   	    }
   	    // fuck man...i lost the other beginning bracket for the one above....
        else if (command.equalsIgnoreCase("home") || command.equalsIgnoreCase("wphome"))
        {
            if (!permissions.has(player, "waypoint.home")) {
                player.sendMessage(ChatColor.BLUE + "You do not have the permissions to use this command.");
                return true;
            }
  	        String subc = "";
    	    try {
    	        subc = args[0];
    	    }
            catch (java.lang.ArrayIndexOutOfBoundsException e) {
                if (home.getProperty(HomeNodeChomp(player, player.getWorld(), "coord.X")) != null)
                {
                    World w = player.getWorld();
                    double x = (Double)home.getProperty(HomeNodeChomp(player, w, "coord.X"));
                    double y = (Double)home.getProperty(HomeNodeChomp(player, w, "coord.Y"));
                    double z = (Double)home.getProperty(HomeNodeChomp(player, w, "coord.Z"));
					double yaw = (Double)home.getProperty(HomeNodeChomp(player, w, "coord.yaw"));
					double pitch = (Double)home.getProperty(HomeNodeChomp(player, w, "coord.pitch"));
                    Location l = new Location(w, x, y, z, (float)yaw, (float)pitch);
                    player.setCompassTarget(l);
                    player.teleport(l);
                    player.sendMessage(ChatColor.GREEN + "Welcome home, " + player.getName().toString() + ".");
                    return true;
                }
                else if (home.getProperty(HomeNodeChomp(player, player.getWorld(), "coord.X")) == null)
                {
                    World w = player.getWorld();
                    home.setProperty(HomeNodeChomp(player, w, "coord.X"), player.getLocation().getX());
                    home.setProperty(HomeNodeChomp(player, w, "coord.Y"), player.getLocation().getY());
                    home.setProperty(HomeNodeChomp(player, w, "coord.Z"), player.getLocation().getZ());
					home.setProperty(HomeNodeChomp(player, w, "coord.yaw"), (double)player.getLocation().getYaw());
					home.setProperty(HomeNodeChomp(player, w, "coord.pitch"), (double)player.getLocation().getPitch());
                    player.sendMessage(ChatColor.AQUA + "[Waypoint] Your home point for world " + w.getName().toString() + " was not set, so it was automatically set to the point you are currently at now.");
                    home.save();
                    return true;
                }
            };
            subc = subc.toLowerCase().toString();
            if (subc.equalsIgnoreCase("set"))
            {
                World w = player.getWorld();
                if (!permissions.has(player, "waypoint.home.set")) {
                    player.sendMessage(ChatColor.BLUE + "You do not have the permissions to use this command.");
                    return true;
                }
                home.setProperty(HomeNodeChomp(player, w, "world"), player.getWorld().getName().toString());
                home.setProperty(HomeNodeChomp(player, w, "coord.X"), player.getLocation().getX());
                home.setProperty(HomeNodeChomp(player, w, "coord.Y"), player.getLocation().getY());
                home.setProperty(HomeNodeChomp(player, w, "coord.Z"), player.getLocation().getZ());
				home.setProperty(HomeNodeChomp(player, w, "coord.yaw"), (double)player.getLocation().getYaw());
				home.setProperty(HomeNodeChomp(player, w, "coord.pitch"), (double)player.getLocation().getPitch());
                player.sendMessage(ChatColor.AQUA + "[Waypoint] Your home location has been set.");
                home.save();
                return true;
            }
            else if (subc.equalsIgnoreCase("help"))
            {
            	player.sendMessage(ChatColor.BLUE + "Waypoint, version " + plugin.getDescription().getVersion());
            	player.sendMessage(ChatColor.GREEN + "/home:");
            	player.sendMessage(ChatColor.RED + "   with args, will teleport you to your home.");
            	player.sendMessage(ChatColor.RED + "   set - sets your home to the location you are currently standing at.");
            	player.sendMessage(ChatColor.GREEN + "ways to set your home:");
            	if (main.getProperty("home.set_home_at_bed") == "true") { player.sendMessage(ChatColor.RED + "getting into a bed will set your home to the position of that bed."); };
            	player.sendMessage(ChatColor.RED + "typing /home without having a home already set.");
            	player.sendMessage(ChatColor.RED + "typing /home set.");
            	return true;
            };
        }
        else if (command.equalsIgnoreCase("setspawn") || command.equalsIgnoreCase("wpsetspawn"))
        {
            if (!permissions.has(player, "waypoint.admin.spawn.set")) {
                player.sendMessage(ChatColor.BLUE + "You do not have the permissions to use this command.");
                return true;
            }
	    String subc = "";
            World w = player.getWorld();
            plugin.spawnManager.SendPlayerToSpawn(w, player);
            player.sendMessage(ChatColor.AQUA + "[Waypoint] Spawn for world " + player.getWorld().getName() + " has been set.");
            return true;
        }
        else if (command.equalsIgnoreCase("spawn") || command.equalsIgnoreCase("wpspawn"))
        {
            if (!permissions.has(player, "waypoint.spawn")) {
                player.sendMessage(ChatColor.BLUE + "You do not have the permissions to use this command.");
                return true;
            }
  	        String subc = "";
    	    try {
    	        subc = args[0];
    	    }
    	    catch (ArrayIndexOutOfBoundsException e) {
    	        World w = player.getWorld();
                plugin.spawnManager.SendPlayerToSpawn(w, player);
    	        return true;
    	    }
    	    World w = plugin.getServer().getWorld((String)subc);
    	    if (w == null)
    	    {
    	        player.sendMessage("[Waypoint] World '" + subc + "' does not exist.");
    	        return true;
    	    }
            plugin.spawnManager.SendPlayerToSpawn(w, player);
            return true;
    	}
    	else if (command.equalsIgnoreCase("spawnadmin") || command.equalsIgnoreCase("wpspawnadmin"))
        {
            if (!permissions.has(player, "waypoint.admin.spawn")) {
                player.sendMessage(ChatColor.BLUE + "You do not have the permissions to use this command.");
                return true;
            }
	        String subc = "";
    	    try {
    	    subc = args[0];
            }
            catch (java.lang.ArrayIndexOutOfBoundsException e) {
    	        player.sendMessage("Usage: /spawnadmin <save|load;set> <world>");
                return true;
    	    };
            subc = subc.toLowerCase().toString();
            String arg = null;
            try {
                arg = args[1];
            }
            catch (java.lang.ArrayIndexOutOfBoundsException e) {
    	        arg = null;
            };
            if (subc.equalsIgnoreCase("save"))
            {
                if (!permissions.has(player, "waypoint.admin.spawn.save")) {
                    player.sendMessage(ChatColor.BLUE + "You do not have the permissions to use this command.");
                    return true;
                }
                if (arg == null)
                {
                    World w = plugin.getServer().getWorld(player.getWorld().getName().toString());
                    plugin.spawnManager.SaveWorldSpawnLocation((World)w);
                    player.sendMessage(ChatColor.BLUE + "[Waypoint] Forcibly saved spawn point for world: " + w.getName().toString());
                    return true;
                }
                World w = plugin.getServer().getWorld(arg);
                if (w == null)
                { player.sendMessage(ChatColor.RED + "[Waypoint] Invalid world: " + arg); return true; }
                plugin.spawnManager.SaveWorldSpawnLocation(w);
                player.sendMessage(ChatColor.BLUE + "[Waypoint] Forcibly saved spawn point for world: " + w.getName().toString());
                return true;
            }
            else if (subc.equalsIgnoreCase("load"))
            {
                if (!permissions.has(player, "waypoint.admin.spawn.load")) {
                    player.sendMessage(ChatColor.BLUE + "You do not have the permissions to use this command.");
                    return true;
                }
                if (arg == null)
                {
                    plugin.spawnManager.LoadWorldSpawnLocation(player.getWorld());
                    player.sendMessage(ChatColor.BLUE + "[Waypoint] Forcibly reloaded spawn point for world: " + player.getWorld().getName().toString());
                    return true;
                }
                plugin.spawnManager.LoadWorldSpawnLocation(player.getWorld());
                player.sendMessage(ChatColor.BLUE + "[Waypoint] Forcibly reloaded spawn point for world: " + player.getWorld().getName().toString());
                return true;
            }
            else if (subc.equalsIgnoreCase("set"))
            {
                if (!permissions.has(player, "waypoint.admin.spawn.set")) {
                    player.sendMessage(ChatColor.BLUE + "You do not have the permissions to use this command.");
                    return true;
                }
                plugin.spawnManager.SetSpawnFromPlayer(player.getWorld(), player);
                player.getWorld().save();
                player.sendMessage(ChatColor.BLUE + "[Waypoint] Set spawn point for world: " + player.getWorld().getName());
                return true;
            }
        }
        else if (command.equalsIgnoreCase("wptp"))
        {
            if (!permissions.has(player, "waypoint.teleport.teleport")) {
                player.sendMessage(ChatColor.BLUE + "You do not have the permissions to use this command.");
                return true;
            }
	        String subc = "";
    	    try {
    	        subc = args[0];
            }
            catch (java.lang.ArrayIndexOutOfBoundsException e) {
    	        player.sendMessage("Usage: /tp <target> [user]");
                return true;
    	    };
    	    // I don't think thise should be here.
    	    //
            //   subc = subc.toLowerCase().toString();
            String arg = null;
            try {
                arg = args[1];
            }
            catch (java.lang.ArrayIndexOutOfBoundsException e) {
    	        arg = null;
            };
            if (arg == null)
            {
                Player target = plugin.getServer().getPlayer((String)subc);
                if (target == null) 
                {
                    player.sendMessage("[Waypoint] Player " + subc + " is not logged in.");
                    return true;
                }
                Location l = target.getLocation();
                player.teleport(l);
                return true;
            }
            else if (arg != null)
            {
                Player p = plugin.getServer().getPlayer((String)subc);
                Player target = plugin.getServer().getPlayer((String)arg);
                if (p == null)
                {
                    player.sendMessage("[Waypoint] Player " + subc + " is not online.");
                    return true;
                }
                else if (target == null)
                {
                    player.sendMessage("[Waypoint] Player " + arg + " is not online.");
                    return true;
                }
                Location l = target.getLocation();
                p.teleport(l);
                p.sendMessage("[Waypoint] You have been teleported by " + player.getName().toString() + ".");
                return true;
            }
        }
        else if (command.equalsIgnoreCase("tploc") || command.equalsIgnoreCase("wptploc"))
        {
            if (!permissions.has(player, "waypoint.teleport.location")) {
                player.sendMessage(ChatColor.BLUE + "You do not have the permissions to use this command.");
                return true;
            }
	        String subc = "";
            String arg = null;
            try {
                arg = args[0];
            }
            catch (java.lang.ArrayIndexOutOfBoundsException e) {
    	        arg = null;
            };
            if (arg != null)
            {
                World w;
                double x;
                double y;
                double z;
                try {
                    String[] d = arg.split("\\,");
                    w = player.getLocation().getWorld();
                    x = new Double(d[0]);
                    y = new Double(d[1]);
                    z = new Double(d[2]);
                }
                catch (java.lang.ArrayIndexOutOfBoundsException e) {
                    player.sendMessage(ChatColor.RED + "[Waypoint] Invalid Coordinates.");
                    return true;
                };
                Location l = new Location((World) w, (Double) x, (Double) y, (Double) z);
                player.teleport(l);
                return true;
            }
            else if (arg == null)
            {
                player.sendMessage(ChatColor.RED + "[Waypoint] You need to provide a set of coordinates.");
                return true;
            }
        }
        else if (command.equalsIgnoreCase("tphere") || command.equalsIgnoreCase("wptphere"))
        {
            if (!permissions.has(player, "waypoint.teleport.here")) {
                player.sendMessage(ChatColor.BLUE + "You do not have the permissions to use this command.");
                return true;
            }
	        String subc = "";
    	    try {
    	        subc = args[0];
            }
            catch (java.lang.ArrayIndexOutOfBoundsException e) {
    	        player.sendMessage("Usage: /tp <target> [user]");
                return true;
    	    };
            subc = subc.toLowerCase().toString();
            Player target = plugin.getServer().getPlayer((String)subc);
            if (target == null)
            {
                player.sendMessage(ChatColor.AQUA + "[Waypoint] Player " + subc + " is not online.");
                return true;
            }
            Location l = player.getLocation();
            Vehicle target_veh = target.getVehicle();
            if (target_veh != null) { target_veh.eject(); };
            target.teleport(l);
            target.sendMessage(ChatColor.GREEN + "[Waypoint] You have been teleported to " + player.getName().toString() + ".");
            return true;
        }
        else if (command.equalsIgnoreCase("setwarp") || command.equalsIgnoreCase("wpsetwarp"))
        {
            if (!permissions.has(player, "waypoint.warp.create")) {
                player.sendMessage(ChatColor.BLUE + "You do not have the permissions to use this command.");
                return true;
            }
	        String subc = "";
    	    try {
    	    subc = args[0];
            }
            catch (java.lang.ArrayIndexOutOfBoundsException e) {
    	        player.sendMessage("Usage: /setwarp <name>");
                return true;
    	    };
            subc = subc.toLowerCase().toString();
            plugin.warpManager.CreateWarp(player, subc);
            player.sendMessage(ChatColor.AQUA + "[Waypoint] Warp " + subc + " has been created.");
            return true;
        }
        else if (command.equalsIgnoreCase("warpadmin") || command.equalsIgnoreCase("wpwarpadmin"))
        {
            if (!permissions.has(player, "waypoint.admin.warp"))
            {
                player.sendMessage(ChatColor.BLUE + "You do not have the permission to use this command.");
                return true;
            }
            String subc = "";
            String arg = "";
            String k = "";
            String v = "";
    	    try {
                subc = args[0];
            }
            catch (java.lang.ArrayIndexOutOfBoundsException e) {
    	        player.sendMessage("Usage: /warpadmin <del|set> [key] [value]");
                return true;
    	    };
    	    // SHOULD *NOT* be here in a case sensitive situation
            // subc = subc.toLowerCase().toString();
            try {
                arg = args[1];
            }
            catch (java.lang.ArrayIndexOutOfBoundsException e) {
    	        arg = null;
            };
            try {
                k = args[2];
                v = args[3];
            }
            catch (java.lang.ArrayIndexOutOfBoundsException e) {
    	        k = null;
    	        v = null;
            }
            if (subc.equalsIgnoreCase("del"))
            {
                if (arg == null)
                {
                    player.sendMessage(ChatColor.RED + "/warpadmin del <warpname>");
                    return true;
                }
                if (warp.getProperty(plugin.warpManager.WarpBase(arg)) == null)
                {
                    player.sendMessage(ChatColor.RED + "[Waypoint] There is no warp by that name.");
                    return true;
                }
                if (!permissions.has(player, "waypoint.admin.warp.delete"))
                {
                    player.sendMessage(ChatColor.RED + "[Waypoint] You do not have permission to use this command.");
                    return true;
                }
                plugin.warpManager.DeleteWarp(arg);
                player.sendMessage(ChatColor.BLUE + "[Waypoint] Warp " + arg + " has been deleted.");
                return true;
            }
            else if (subc.equalsIgnoreCase("read"))
            {
                if (arg == null)
                {
                    player.sendMessage(ChatColor.RED + "/warp read <warpname>");
                    return true;
                }
                ConfigurationNode a = warp.getNode(plugin.warpManager.WarpBase(arg));
                if (a == null)
                {
                    player.sendMessage("[Waypoint] No warp by that name.");
                    return true;
                }
                player.sendMessage(ChatColor.BLUE + "[Waypoint] Metadata for warp '" + arg + "':");
                Map<String, Object> b = a.getAll();
                for (Map.Entry<String, Object> entry : b.entrySet())
                {
                    player.sendMessage(String.format("%s  %s -> %s%s", ChatColor.GREEN, (String) entry.getKey(), ChatColor.BLUE, (String) entry.getValue().toString()));
                }
                return true;
            }
            else if (subc.equalsIgnoreCase("set"))
            {
                if (arg == null)
                {
                    player.sendMessage(ChatColor.RED + "/warp set <warpname> <key> <value>");
                    return true;
                }
                else if (k == null)
                {
                    player.sendMessage(ChatColor.RED + "/warp set <warpname> <key> <value>");
                    return true;
                }
                else if (v == null)
                {
                    player.sendMessage(ChatColor.RED + "/warp set <warpname> <key> <value>");
                    return true;
                }
                // XXX - remove this for:
                //   custom properties?
                //
                // else if (!k.equalsIgnoreCase("owner") || !k.equalsIgnoreCase("permission"))
                // {
                //    player.sendMessage(ChatColor.GREEN + "Settable warp options:");
                //    player.sendMessage(ChatColor.BLUE + " - owner -- the owner of the warp.");
                //    player.sendMessage(ChatColor.BLUE + " - permission -- who can use this warp.");
                //    return true;
                // }
                String owner = (String) warp.getProperty(plugin.warpManager.WarpNode(arg, "owner"));
                // Well, this is about to get fixed. (protip, double-slashes work well. ERRYWHERE.
                //
                // if (!owner.equals(player.getName().toString()))
                // {
                //     player.sendMessage(ChatColor.RED + "[Waypoint] You do not have permission modify this warp.");
                //     return true;
                // }
                k = k.toLowerCase().toString();
                // nope, this is stupid.
                // v = v.toLowerCase().toString();
                if (warp.getProperty(plugin.warpManager.WarpNode(arg, "permission")) == null)
                {
                    player.sendMessage(ChatColor.RED + "[Waypoint] This warp does not exist.");
                    return true;
                }
                boolean f = plugin.warpManager.SetWarpProp(arg, k, v);
                if (!f)
                {
                    player.sendMessage(ChatColor.RED + "[Waypoint] An error occurred while trying to set the properties of warp: " + arg);
                    return true;
                }
                else if (f)
                {
                    player.sendMessage(ChatColor.GREEN + "[Waypoint] Successfully set the property '" + k + "' to '" + v + "' for warp " + arg);
                    return true;
                }
                return true;
            }
        }
        else if (command.equalsIgnoreCase("world") || command.equalsIgnoreCase("wpworld"))
        {
            if (!permissions.has(player, "waypoint.world")) {
                player.sendMessage(ChatColor.BLUE + "You do not have the permissions to use this command.");
                return true;
            }
            String subc = "";
            String arg = "";
            try {
                subc = args[0];
            }
            catch (java.lang.ArrayIndexOutOfBoundsException e) {
                subc = null;
            }
            if (subc == null)
            {
                String worldname = (String) player.getLocation().getWorld().getName().toString();
                player.sendMessage(ChatColor.BLUE + "You are currently in world: " + worldname);
                String x = (String) Double.toString(player.getLocation().getX());
                String y = (String) Double.toString(player.getLocation().getY());
                String z = (String) Double.toString(player.getLocation().getZ());
                player.sendMessage(ChatColor.BLUE + "Your current position is: " + x + "," + y + "," + z);
                return true;
            }
            else if (subc.equalsIgnoreCase("list"))
            {
               // @accepts [0 args]
               List<World> w = plugin.getServer().getWorlds();
               player.sendMessage(ChatColor.GREEN + "World List: ");
               Iterator i = w.iterator();
               World wx;
               while (i.hasNext())
               {
                   wx = (World) i.next();
                   player.sendMessage(ChatColor.GREEN + " - " + wx.getName());
               }
               return true;
            }
            else if (subc.equalsIgnoreCase("import"))
            {
                if (!permissions.has(player, "waypoint.admin.world.import")) {
                    player.sendMessage(ChatColor.BLUE + "You do not have the permissions to use this command.");
                    return true;
                }
                // @accepts [2 args]: worldname, environment
                String worldname = args[1];
                String environ;
                try { environ = args[2]; }
                catch (java.lang.ArrayIndexOutOfBoundsException e) { environ = null; };
                if (environ == null)
                {
                    player.sendMessage(ChatColor.BLUE + "[Waypoint] Please specify an environment type.");
                    return true;
                }
                World wx = plugin.worldManager.Import(worldname, ((String) environ).toUpperCase());
                if (wx == null)
                {
                    player.sendMessage(ChatColor.RED + "World import failed.");
                    return true;
                }
                plugin.config.getWorld().setProperty("world." + worldname + ".env", environ.toUpperCase());
                plugin.config.save();
                player.sendMessage(String.format("%s[Waypoint] Loaded world: { %s [ENV:%s] }", ChatColor.GREEN, worldname, environ.toUpperCase()));
                return true;
            }
            else if (subc.equalsIgnoreCase("create"))
            {
                if (!permissions.has(player, "waypoint.admin.world.create")) {
                    player.sendMessage(ChatColor.BLUE + "You do not have the permissions to use this command.");
                    return true;
                }
                // @accepts [2-3 args]: worldname, environment [, seed]
                String worldname = args[1];
                String environ;
                try { environ = args[2]; }
                catch (java.lang.ArrayIndexOutOfBoundsException e) { environ = null; };
                String seed;
                World wx = null;
                try {
                    seed = args[3];
                }
                catch (java.lang.ArrayIndexOutOfBoundsException e)
                {
                    seed = null;
                }
                if (environ == null)
                {
                    player.sendMessage(ChatColor.BLUE + "[Waypoint] Please specify an environment type.");
                    return true;
                }
                if (seed != null)
                {
                    wx = plugin.worldManager.Create(worldname, environ.toUpperCase(), seed);
                }
                else if (seed == null)
                {
                    wx = plugin.worldManager.Create(worldname, environ.toUpperCase());
                }
                if (wx == null)
                {
                    player.sendMessage(String.format("%s[Waypoint] Could not create world: %s", ChatColor.RED, worldname));
                    return true;
                }
                plugin.config.getWorld().setProperty("world." + worldname + ".env", environ.toUpperCase());
                plugin.config.save();
                player.sendMessage(String.format("%s[Waypoint] Successfully created world: { %s [ENV: %s] }", ChatColor.GREEN, worldname, environ.toUpperCase()));
                return true;
            }
            else if (subc != null)
            {
               World w = plugin.getServer().getWorld(subc);
               if (w == null)
               {
                   player.sendMessage(ChatColor.RED + "[Waypoint] World " + subc + " does not exist.");
                   return true;
               }
               if (!permissions.has(player, "waypoint.world.teleport"))
               {
                   player.sendMessage("[Waypoint] You do not have permission to use this command.");
                   return true;
               }
               Location wsl = w.getSpawnLocation();
               player.teleport(wsl);
               player.sendMessage(ChatColor.GREEN + "[Waypoint] You have been taken to the spawn of world '" + (String) w.getName().toString() + "'.");
               return true;
           }
        }
        else if (command.equalsIgnoreCase("warp") || command.equalsIgnoreCase("wpwarp"))
        {
            if (!permissions.has(player, "waypoint.warp")) {
                player.sendMessage(ChatColor.BLUE + "You do not have the permissions to use this command.");
                return true;
            }
    	    String subc = "";
	        String arg = "";
	        String k = "";
	        String v = "";
    	    try {
                subc = args[0];
            }
            catch (java.lang.ArrayIndexOutOfBoundsException e) {
    	        player.sendMessage("Usage: /warp <add|del|set|list|<warp name>> [key] [value]");
                return true;
    	    };
            String origc = subc;
            // again, this is a case sensitive situation.
            // subc = subc.toLowerCase().toString();
            try {
                arg = args[1];
            }
            catch (java.lang.ArrayIndexOutOfBoundsException e) {
    	        arg = null;
            };
            try {
                k = args[2];
                v = args[3];
            }
            catch (java.lang.ArrayIndexOutOfBoundsException e) {
    	        k = null;
    	        v = null;
            }
            if (subc.equalsIgnoreCase("add"))
            {
                if (arg == null)
                {
                    player.sendMessage(ChatColor.RED + "/warp add <warpname>");
                    return true;
                }
                if (!permissions.has(player, "waypoint.warp.create"))
                {
                    player.sendMessage(ChatColor.RED + "[Waypoint] You do not have permission to use this command.");
                    return true;
                }
                plugin.warpManager.CreateWarp(player, arg);
                player.sendMessage(ChatColor.AQUA + "[Waypoint] Warp " + arg + " has been created.");
                return true;
            }
            else if (subc.equalsIgnoreCase("del"))
            {
                if (arg == null)
                {
                    player.sendMessage(ChatColor.RED + "/warp del <warpname>");
                    return true;
                }
                if (warp.getProperty(plugin.warpManager.WarpBase(arg)) == null)
                {
                    player.sendMessage(ChatColor.RED + "[Waypoint] There is no warp by that name.");
                    return true;
                }
                String owner = (String)warp.getProperty(plugin.warpManager.WarpNode(arg, "owner"));
                if (!permissions.has(player, "waypoint.warp.delete") || !owner.equals(player.getName().toString()))
                {
                    player.sendMessage(ChatColor.RED + "[Waypoint] You do not have permission to use this command.");
                    return true;
                }
                plugin.warpManager.DeleteWarp(arg);
                player.sendMessage(ChatColor.AQUA + "[Waypoint] Warp " + arg + " has been deleted.");
                return true;
            }
            else if (subc.equalsIgnoreCase("set"))
            {
                if (arg == null)
                {
                    player.sendMessage(ChatColor.RED + "/warp set <warpname> <key> <value>");
                    return true;
                }
                else if (k == null)
                {
                    player.sendMessage(ChatColor.RED + "/warp set <warpname> <key> <value>");
                    return true;
                }
                else if (v == null)
                {
                    player.sendMessage(ChatColor.RED + "/warp set <warpname> <key> <value>");
                    return true;
                }
                // XXX - remove this for:
                //   custom properties?
                //
                // else if (!k.equalsIgnoreCase("owner") || !k.equalsIgnoreCase("permission"))
                // {
                //    player.sendMessage(ChatColor.GREEN + "Settable warp options:");
                //    player.sendMessage(ChatColor.BLUE + " - owner -- the owner of the warp.");
                //    player.sendMessage(ChatColor.BLUE + " - permission -- who can use this warp.");
                //    return true;
                // }
                String owner = (String) warp.getProperty(plugin.warpManager.WarpNode(arg, "owner"));
                if (!owner.equals(player.getName().toString()))
                {
                    player.sendMessage(ChatColor.RED + "[Waypoint] You do not have permission modify this warp.");
                    return true;
                }
                // WOW I'M SUCH A FREAKING IDIOT. IT TOOK 5 BUILDS 
                // TO FIGURE THIS SHIT OUT.
                //
                // k = k.toLowerCase().toString();
                // v = v.toLowerCase().toString();
                if (warp.getProperty(plugin.warpManager.WarpNode(arg, "permission")) == null)
                {
                    player.sendMessage(ChatColor.RED + "[Waypoint] This warp does not exist.");
                    return true;
                }
                boolean f = plugin.warpManager.SetWarpProp(arg, k, v);
                if (!f)
                {
                    player.sendMessage(ChatColor.RED + "[Waypoint] An error occurred while trying to set the properties of warp: " + arg);
                    return true;
                }
                else if (f)
                {
                    player.sendMessage(ChatColor.GREEN + "[Waypoint] Successfully set the property '" + k + "' to '" + v + "' for warp " + arg);
                    return true;
                }
                return true;
            }
            else if (subc.equalsIgnoreCase("list"))
            {
                if (!permissions.has(player, "waypoint.warp.list"))
                {
                    player.sendMessage(ChatColor.RED + "[Waypoint] You do not have the permission to access this command.");
                    return true;
                }
                int x = 0;
                Map<String, ConfigurationNode> a;
                a = warp.getNodes("warps");
                if (a == null || a.size() == 0)
                {
                   player.sendMessage(ChatColor.YELLOW + "[Waypoint] There are currently no warps to be displayed.");
                   return true;
                }
                player.sendMessage(ChatColor.GREEN + "====[Waypoint] Warps available to you:====");
                for (Map.Entry<String, ConfigurationNode> entry : a.entrySet())
                {
                    ConfigurationNode node = entry.getValue();
                    String warppermission = (String) node.getProperty("permission");
                    if (warppermission == null)
                    {
                       player.sendMessage(ChatColor.RED + "[Waypoint] You have not set any warp permission groups to your configuration.");
                       return true;
                    }
                    if (plugin.warpManager.checkperms(player, warppermission))
                    {
                        if (!player.getWorld().toString().equals((String)node.getProperty("world")) && ((String) plugin.config.getMain().getProperty("warp.list_world_only")).equals("true"))
                        {
                            continue;
                        }
                        else
                        {
                            player.sendMessage(ChatColor.AQUA + " - " + entry.getKey());
                        }
                    }
                    x++;
                }
                if (x == 0)
                {
                   player.sendMessage(ChatColor.YELLOW + "[Waypoint] There are currently no warps to be displayed.");
                }
                return true;
            }
            // TODO: case sensitivity check
            else if (warp.getProperty(plugin.warpManager.WarpBase(origc)) != null)
            {
                plugin.warpManager.PlayerToWarp(player, (String)origc);
                return true;
            }
            else if (warp.getProperty(plugin.warpManager.WarpBase(origc)) == null)
            {
                player.sendMessage(ChatColor.LIGHT_PURPLE + "[Waypoint] Warp " + origc + " does not exist.");
                return true;
            }
        }
        return false;
    };
};