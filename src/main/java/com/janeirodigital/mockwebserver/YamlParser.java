package com.janeirodigital.mockwebserver;

import org.yaml.snakeyaml.Yaml;

class YamlParser implements Parser {
    @Override
    public Fixture parse(String string) {
        return new Yaml().loadAs(string, Fixture.class);
    }
}
