package com.toonystank.bungeeutils.utils.config.file;

import com.toonystank.bungeeutils.utils.config.ConfigurationSection;
import com.toonystank.bungeeutils.utils.config.serialization.ConfigurationSerializable;
import com.toonystank.bungeeutils.utils.config.serialization.ConfigurationSerialization;
import org.jetbrains.annotations.NotNull;
import java.util.LinkedHashMap;
import java.util.Map;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.representer.Representer;

public class YamlRepresenter extends Representer {

    public YamlRepresenter() {
        this.multiRepresenters.put(ConfigurationSection.class, new RepresentConfigurationSection());
        this.multiRepresenters.put(ConfigurationSerializable.class, new RepresentConfigurationSerializable());
        // SPIGOT-6234: We could just switch YamlConstructor to extend Constructor rather than SafeConstructor, however there is a very small risk of issues with plugins treating config as untrusted input
        // So instead we will just allow future plugins to have their enums extend ConfigurationSerializable
        this.multiRepresenters.remove(Enum.class);
    }

    // SPIGOT-6949: Used by configuration sections that are nested within lists or maps.
    private class RepresentConfigurationSection extends RepresentMap {

        @NotNull
        @Override
        public Node representData(@NotNull Object data) {
            return super.representData(((ConfigurationSection) data).getValues(false));
        }
    }

    private class RepresentConfigurationSerializable extends RepresentMap {

        @NotNull
        @Override
        public Node representData(@NotNull Object data) {
            ConfigurationSerializable serializable = (ConfigurationSerializable) data;
            Map<String, Object> values = new LinkedHashMap<String, Object>();
            values.put(ConfigurationSerialization.SERIALIZED_TYPE_KEY, ConfigurationSerialization.getAlias(serializable.getClass()));
            values.putAll(serializable.serialize());

            return super.representData(values);
        }
    }
}