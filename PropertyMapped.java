import java.util.Map;

public interface PropertyMapped {
    void build(Map<String, String> propMap);
    Map<String, String> getPropMap();
}
