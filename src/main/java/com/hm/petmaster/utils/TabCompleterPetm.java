package utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.eclipse.jdt.annotation.NonNull;

public class TabCompleterPetm implements TabCompleter{
	
	@Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias,  String[] args) {
        List<String> list = Arrays.asList("help", "info" ,"reload", "disable", "enable", " setowner", "free", "setcolor", "sharepet", "godpet", "petskill");
        String input = args[0].toLowerCase();

        List<String> completions = null;
        for (String s : list) {
            if (s.startsWith(input)) {
                if (completions == null) {
                    completions = new ArrayList<>();
                }
                completions.add(s);
            }
        }
        if (completions != null) {
            Collections.sort(completions);
            return completions;
        }
        return completions;
    }

}
