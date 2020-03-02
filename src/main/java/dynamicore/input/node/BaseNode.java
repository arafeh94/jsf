package dynamicore.input.node;


import com.arafeh.jsf.model.NodeType;
import org.primefaces.json.JSONObject;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.arafeh.jsf.core.utils.Extensions.isNullOrEmpty;
import static com.arafeh.jsf.core.utils.Extensions.nullOr;

public abstract class BaseNode<T> implements LinkedNodeInterface<T> {
    protected long id;
    protected long projectId;
    protected InputLocation inputLocation;
    protected boolean isDead;
    protected String reason;
    protected String json;
    protected String source;
    protected NodeType type;
    protected boolean scannable = true;
    protected boolean fetched = false;
    protected String customType;
    protected String customRelation;
    protected String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCustomType() {
        return customType;
    }

    public void setCustomType(String customType) {
        this.customType = customType;
    }

    public String getCustomRelation() {
        return customRelation;
    }

    public void setCustomRelation(String customRelation) {
        this.customRelation = customRelation;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public InputLocation getInputLocation() {
        return inputLocation;
    }

    public void setInputLocation(InputLocation inputLocation) {
        this.inputLocation = inputLocation;
    }

    public boolean isDead() {
        return isDead;
    }

    public void setDead(boolean dead) {
        isDead = dead;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getJson() {
        return json;
    }


    public void setJson(String json) {
        this.json = json;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public NodeType getType() {
        return type;
    }

    public void setType(NodeType type) {
        this.type = type;
    }

    public void setFetched(boolean fetched) {
        this.fetched = fetched;
    }

    public boolean isFetched() {
        return fetched;
    }

    public void setScannable(boolean scannable) {
        this.scannable = scannable;
    }

    public boolean isScannable() {
        return scannable;
    }

    @Override
    public String getCompositeId() {
        Pattern pattern = Pattern.compile("\\{.*?}");
        Matcher matcher = pattern.matcher(compositeFormula());
        StringBuilder values = new StringBuilder();
        final Object copy = this;
        while (matcher.find()) {
            String fieldName = matcher.group().replaceAll("[{}]", "");
            Optional<Field> fieldOpt = this.field(fieldName);
            if (fieldOpt.isPresent()) {
                Field field = fieldOpt.get();
                field.setAccessible(true);
                try {
                    String value = field.get(copy).toString();
                    if (value.equals("") || value.equals("-1")) throw new RuntimeException("invalid value");
                    values.append(value).append(":");
                } catch (Exception ignore) {

                }
            } else {
                String value = getCompositeAttrValue(fieldName);
                if (!isNullOrEmpty(value)) values.append(value).append(":");
            }
        }
        return values.substring(0, values.length() - 1);
    }

    public void setCompositeId(HashMap<String, Object> map) {
        for (String fieldName : map.keySet()) {
            this.field(fieldName).ifPresent(field -> {
                field.setAccessible(true);
                try {
                    field.set(this, map.get(fieldName));
                } catch (Exception ignore) {
                }
            });
        }
    }

    protected String getCompositeAttrValue(String property) {
        return null;
    }

    private Optional<Field> field(String fieldName) {
        Field field = null;
        try {
            field = this.getClass().getDeclaredField(fieldName);
        } catch (Exception e) {
            try {
                field = this.getClass().getSuperclass().getDeclaredField(fieldName);
            } catch (Exception ignore) {
            }
        }
        return Optional.ofNullable(field);
    }


    public InputLocation location() {
        return getInputLocation();
    }

    public abstract String compositeFormula();

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj instanceof InputNode) {
            return ((InputNode) obj).getId() == this.getId();
        }
        return false;
    }

    @Override
    public String toString() {
        return "BaseNode{" +
                "id=" + id +
                ", projectId=" + projectId +
                ", inputLocation=" + inputLocation +
                ", json='" + json + '\'' +
                ", type=" + type +
                '}';
    }
}
