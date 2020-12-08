package be.woutzah.chatbrawl.races.types;

import be.woutzah.chatbrawl.ChatBrawl;
import be.woutzah.chatbrawl.exceptions.RaceException;
import be.woutzah.chatbrawl.races.Race;
import com.google.common.base.Enums;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;

import java.util.*;

public class HuntRace extends Race {

    private final ChatBrawl plugin;
    private HashMap<EntityType, Integer> huntMap;
    private HashMap<UUID, Integer> playerScores;
    private EntityType currentEntityType;
    private int currentAmount;
    private FileConfiguration huntRaceConfig;
    private String huntraceName;
    private List<String> huntraceStart;
    private String huntraceEnd;
    private List<String> huntraceWinner;
    private String huntraceWinnerPersonal;
    private String huntraceActionBar;

    public HuntRace(ChatBrawl plugin, FileConfiguration config) {
        super(
                plugin,
                config.getLong("huntrace.duration"),
                config.getInt("huntrace.chance"),
                config.getBoolean("huntrace.enable-firework"),
                config.getBoolean("huntrace.enabled"),
                config.getConfigurationSection("huntrace.rewards.commands")
        );
        this.plugin = plugin;
        this.huntRaceConfig = config;
        this.playerScores = new HashMap<>();
        this.huntMap = new HashMap<>();
        getMobsFromConfig();
        initializeLanguageEntries();
    }

    private void initializeLanguageEntries() {
        this.huntraceName = huntRaceConfig.getString("language.huntrace-name");
        this.huntraceStart = huntRaceConfig.getStringList("language.huntrace-start");
        this.huntraceEnd = huntRaceConfig.getString("language.huntrace-ended");
        this.huntraceWinner = huntRaceConfig.getStringList("language.huntrace-winner");
        this.huntraceWinnerPersonal = huntRaceConfig.getString("language.huntrace-winner-personal");
        this.huntraceActionBar = huntRaceConfig.getString("language.huntrace-actionbar-start");
    }

    private void getMobsFromConfig() {
        try {
            final ConfigurationSection configSection = huntRaceConfig.getConfigurationSection("huntrace.mobs");

            for (final String entityString : Objects.requireNonNull(configSection).getKeys(false)) {
                final EntityType entityType = Enums.getIfPresent(EntityType.class, entityString).orNull();

                if (entityType == null || !entityType.isAlive()) {
                    throw new RaceException("Entity is not a mob in hunt race: " + entityString);
                }

                huntMap.put(entityType, Math.max(1, configSection.getInt(entityString)));
            }
        } catch (RaceException e) {
            RaceException.handleConfigException(plugin, e);
        } catch (IllegalArgumentException e) {
            RaceException.handleConfigException(plugin, new RaceException("Invalid mob name in hunt race!"));
        }
    }

    public void generateNewMobPair() {
        Object[] mobs = huntMap.keySet().toArray();
        Object key = mobs[new Random().nextInt(mobs.length)];
        currentEntityType = EntityType.valueOf(key.toString());
        currentAmount = huntMap.get(key);
    }

    public void fillOnlinePlayers() {
        Bukkit.getOnlinePlayers().forEach(p -> playerScores.put(p.getUniqueId(), 0));
    }

    public void removeOnlinePlayers() {
        playerScores.clear();
    }

    public EntityType getCurrentEntityType() {
        return currentEntityType;
    }

    public int getCurrentAmount() {
        return currentAmount;
    }

    public HashMap<UUID, Integer> getPlayerScores() {
        return playerScores;
    }

    public String getHuntraceName() {
        return huntraceName;
    }

    public List<String> getHuntraceStart() {
        return huntraceStart;
    }

    public String getHuntraceEnd() {
        return huntraceEnd;
    }

    public List<String> getHuntraceWinner() {
        return huntraceWinner;
    }

    public String getHuntraceWinnerPersonal() {
        return huntraceWinnerPersonal;
    }

    public String getHuntraceActionBar() {
        return huntraceActionBar;
    }
}
