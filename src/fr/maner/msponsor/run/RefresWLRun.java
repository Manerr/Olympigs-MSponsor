package fr.maner.msponsor.run;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import com.google.gson.JsonObject;

import fr.maner.msponsor.MSponsor;
import fr.maner.msponsor.utils.Web;

public class RefresWLRun implements Runnable {

	private MSponsor pl;

	public RefresWLRun(MSponsor pl) {
		this.pl = pl;
	}

	@Override
	public void run() {
		refreshWL(null);
	}

	public void refreshWL(CommandSender sender) {
		getPseudos(pseudos -> {
			pl.setWLWeb(pseudos);
			String msg;

			if(pseudos.isEmpty())
				msg = "§6» §eLa liste des sponsors est vide";

			else
				msg = "§6» §a" + pl.getWebWL().size() + " joueurs sont présent dans la whitelist";

			if(sender != null)
				sender.sendMessage(msg);
		});
	}

	public void getPseudos(Consumer<Set<String>> consumer) {
		Bukkit.getScheduler().runTaskAsynchronously(pl, () -> {
			Set<String> pseudos = new HashSet<String>();

			String url = pl.urlRQ + "[";
			for (int i = 0; i < pl.getGamesId().size(); i++) {
				url += i == 0 ? pl.getGamesId().get(i) : "," + pl.getGamesId().get(i);
			}
			url += "]";

			JsonObject object = Web.getDataURL(url);

			if(object != null)
				object.get("pseudo").getAsJsonArray().forEach(el -> pseudos.add(el.getAsString()));

			Bukkit.getScheduler().runTask(pl, () -> consumer.accept(pseudos));
		});
	}
}
