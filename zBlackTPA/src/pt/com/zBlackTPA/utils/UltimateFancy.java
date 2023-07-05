package pt.com.zBlackTPA.utils;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * Class to generate JSON elements to use with UltimateChat.
 *
 * @author FabioZumbi12
 */

@SuppressWarnings("unchecked")
public class UltimateFancy {

    private ChatColor lastColor = ChatColor.WHITE;
    private JSONArray constructor;
    private HashMap<String, Boolean> lastformats;
    private List<JSONObject> workingGroup;
    private List<ExtraElement> pendentElements;


    public UltimateFancy() {
        constructor = new JSONArray();
        workingGroup = new ArrayList<>();
        lastformats = new HashMap<>();
        pendentElements = new ArrayList<>();
    }


    public UltimateFancy(String text) {
        constructor = new JSONArray();
        workingGroup = new ArrayList<>();
        lastformats = new HashMap<>();
        pendentElements = new ArrayList<>();
        text(text);
    }


    public UltimateFancy coloredTextAndNext(String text) {
        text = ChatColor.translateAlternateColorCodes('&', text);
        return this.textAndNext(text);
    }


    public UltimateFancy textAndNext(String text) {
        this.text(text);
        return next();
    }


    public UltimateFancy coloredText(String text) {
        text = ChatColor.translateAlternateColorCodes('&', text);
        return this.text(text);
    }

    private List<JSONObject> parseColors(String text) {
        List<JSONObject> jsonList = new ArrayList<>();
        for (String part : text.split("(?=" + ChatColor.COLOR_CHAR + ")")) {
            JSONObject workingText = new JSONObject();

            //fix colors before
            filterColors(workingText);

            Matcher match = Pattern.compile("^" + ChatColor.COLOR_CHAR + "([0-9a-fA-Fk-oK-ORr]).*$").matcher(part);
            if (match.find()) {
                lastColor = ChatColor.getByChar(match.group(1).charAt(0));
                //fix colors from latest
                filterColors(workingText);
                if (part.length() == 2) continue;
            }
            //continue if empty
            if (ChatColor.stripColor(part).isEmpty()) {
                continue;
            }

            workingText.put("text", ChatColor.stripColor(part));

            //fix colors after
            filterColors(workingText);

            if (!workingText.containsKey("color")) {
                workingText.put("color", "white");
            }
            jsonList.add(workingText);
        }
        return jsonList;
    }

    public UltimateFancy text(String text) {
        workingGroup.addAll(parseColors(text));
        return this;
    }

    public UltimateFancy textAtStart(String text) {
        JSONArray jarray = new JSONArray();
        for (JSONObject jobj : parseColors(text)) {
            jarray.add(jobj);
        }
        for (JSONObject jobj : getStoredElements()) {
            jarray.add(jobj);
        }
        this.constructor = jarray;
        return this;
    }

    public UltimateFancy appendObject(JSONObject json) {
        workingGroup.add(json);
        return this;
    }

    public UltimateFancy appendString(String jsonObject) {
        Object obj = JSONValue.parse(jsonObject);
        if (obj instanceof JSONObject) {
            workingGroup.add((JSONObject) obj);
        }
        if (obj instanceof JSONArray) {
            for (Object object : ((JSONArray) obj)) {
                if (object.toString().isEmpty()) continue;
                if (object instanceof JSONArray) {
                    appendString(object.toString());
                } else {
                    workingGroup.add((JSONObject) JSONValue.parse(object.toString()));
                }
            }
        }
        return this;
    }

    public List<JSONObject> getWorkingElements() {
        return this.workingGroup;
    }

    public List<JSONObject> getStoredElements() {
        return new ArrayList<JSONObject>(this.constructor);
    }

    public UltimateFancy removeObject(JSONObject json) {
        this.workingGroup.remove(json);
        this.constructor.remove(json);
        return this;
    }

    public UltimateFancy appendAtFirst(String json) {
        Object obj = JSONValue.parse(json);
        if (obj instanceof JSONObject) {
            appendAtFirst((JSONObject) obj);
        }
        if (obj instanceof JSONArray) {
            for (Object object : ((JSONArray) obj)) {
                if (object.toString().isEmpty()) continue;
                appendAtFirst((JSONObject) JSONValue.parse(object.toString()));
            }
        }
        return this;
    }

    public UltimateFancy appendAtFirst(JSONObject json) {
        JSONArray jarray = new JSONArray();
        jarray.add(json);
        jarray.addAll(getStoredElements());
        this.constructor = jarray;
        return this;
    }

    public UltimateFancy appendAtEnd(String json) {
        Object obj = JSONValue.parse(json);
        if (obj instanceof JSONObject) {
            appendAtEnd((JSONObject) obj);
        }
        if (obj instanceof JSONArray) {
            for (Object object : ((JSONArray) obj)) {
                if (object.toString().isEmpty()) continue;
                appendAtEnd((JSONObject) JSONValue.parse(object.toString()));
            }
        }
        return this;
    }

    public UltimateFancy appendAtEnd(JSONObject json) {
        List<JSONObject> jarray = new ArrayList<>(getWorkingElements());
        jarray.add(json);
        this.workingGroup = jarray;
        return this;
    }

    public List<UltimateFancy> getFancyElements() {
        next();
        List<UltimateFancy> list = new ArrayList<>();
        for (Object obj : this.constructor) {
            if (obj instanceof JSONObject) {
                list.add(new UltimateFancy().appendAtEnd((JSONObject) obj));
            }
        }
        return list;
    }

    public UltimateFancy appendFancy(UltimateFancy fancy) {
        this.appendAtEnd(fancy.toJson());
        return this;
    }

    private void filterColors(JSONObject obj) {
        for (Entry<String, Boolean> format : lastformats.entrySet()) {
            obj.put(format.getKey(), format.getValue());
        }
        if (lastColor.isColor()) {
            obj.put("color", lastColor.name().toLowerCase());
        }
        if (lastColor.isFormat()) {
            String formatStr = lastColor.name().toLowerCase();
            if (lastColor.equals(ChatColor.MAGIC)) {
                formatStr = "obfuscated";
            }
            if (lastColor.equals(ChatColor.UNDERLINE)) {
                formatStr = "underlined";
            }
            lastformats.put(formatStr, true);
            obj.put(formatStr, true);
        }
        if (lastColor.equals(ChatColor.RESET)) {
            obj.put("color", "white");
            for (String format : lastformats.keySet()) {
                lastformats.put(format, false);
                obj.put(format, false);
            }
        }
    }

    public void send(CommandSender to) {
        next();
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + to.getName() + " " + toJson());
    }
    
    public void broadcast() {
    	next();
    	Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw @a " + toJson());
    }
   
    
    @Override
    public String toString() {
        return this.toJson();
    }

    private String toJson() {
        next();
        return "[\"\"," + constructor.toJSONString().substring(1);
    }

    public UltimateFancy next() {
        if (workingGroup.size() > 0) {
            for (JSONObject obj : workingGroup) {
                if (obj.containsKey("text") && obj.get("text").toString().length() > 0) {
                    for (ExtraElement element : pendentElements) {
                        obj.put(element.getAction(), element.getJson());
                    }
                    constructor.add(obj);
                }
            }
        }
        workingGroup = new ArrayList<>();
        pendentElements = new ArrayList<>();
        return this;
    }

    public UltimateFancy clickRunCmd(String cmd) {
        pendentElements.add(new ExtraElement("clickEvent", parseJson("run_command", cmd)));
        return this;
    }

    public UltimateFancy clickSuggestCmd(String cmd) {
        pendentElements.add(new ExtraElement("clickEvent", parseJson("suggest_command", cmd)));
        return this;
    }

    public UltimateFancy clickOpenURL(URL url) {
        pendentElements.add(new ExtraElement("clickEvent", parseJson("open_url", url.toString())));
        return this;
    }

    public UltimateFancy hoverShowText(String text) {
        pendentElements.add(new ExtraElement("hoverEvent", parseHoverText(text)));
        return this;
    }

    public UltimateFancy hoverShowItem(ItemStack item) {
        pendentElements.add(new ExtraElement("hoverEvent", parseHoverItem(item)));
        return this;
    }

    public String toOldFormat() {
        StringBuilder result = new StringBuilder();
        for (Object mjson : constructor) {
            JSONObject json = (JSONObject) mjson;
            if (!json.containsKey("text")) continue;

            try {
                String colorStr = json.get("color").toString();
                if (ChatColor.valueOf(colorStr.toUpperCase()) != null) {
                    ChatColor color = ChatColor.valueOf(colorStr.toUpperCase());
                    if (color.equals(ChatColor.WHITE)) {
                        result.append(String.valueOf(ChatColor.RESET));
                    } else {
                        result.append(String.valueOf(color));
                    }
                }

                for (ChatColor frmt : ChatColor.values()) {
                    if (frmt.isColor()) continue;
                    String frmtStr = frmt.name().toLowerCase();
                    if (frmt.equals(ChatColor.MAGIC)) {
                        frmtStr = "obfuscated";
                    }
                    if (frmt.equals(ChatColor.UNDERLINE)) {
                        frmtStr = "underlined";
                    }
                    if (json.containsKey(frmtStr)) {
                        result.append(String.valueOf(frmt));
                    }
                }
            } catch (Exception ignored) {
            }

            result.append(json.get("text").toString());
        }
        return result.toString();
    }

    private JSONObject parseHoverText(String text) {
        JSONArray extraArr = addColorToArray(ChatColor.translateAlternateColorCodes('&', text));
        JSONObject objExtra = new JSONObject();
        objExtra.put("text", "");
        objExtra.put("extra", extraArr);
        JSONObject obj = new JSONObject();
        obj.put("action", "show_text");
        obj.put("value", objExtra);
        return obj;
    }

    private JSONObject parseJson(String action, String value) {
        JSONObject obj = new JSONObject();
        obj.put("action", action);
        obj.put("value", value);
        return obj;
    }

    private JSONObject parseHoverItem(ItemStack item) {
        JSONObject obj = new JSONObject();
        obj.put("action", "show_item");
        obj.put("value", convertItemStackToJson(item));
        return obj;
    }

    @SuppressWarnings("deprecation")
	private String convertItemStackToJson(ItemStack itemStack) {
        Class<?> craftItemStackClazz = ReflectionUtil.getOBCClass("inventory.CraftItemStack");
        Method asNMSCopyMethod = ReflectionUtil.getMethod(craftItemStackClazz, "asNMSCopy", ItemStack.class);

        Class<?> nmsItemStackClazz = ReflectionUtil.getNMSClass("ItemStack");
        Class<?> nbtTagCompoundClazz = ReflectionUtil.getNMSClass("NBTTagCompound");
        Method saveNmsItemStackMethod = ReflectionUtil.getMethod(nmsItemStackClazz, "save", nbtTagCompoundClazz);

        Object nmsNbtTagCompoundObj;
        Object nmsItemStackObj;
        Object itemAsJsonObject;

        try {
            nmsNbtTagCompoundObj = nbtTagCompoundClazz.newInstance();
            nmsItemStackObj = asNMSCopyMethod.invoke(null, itemStack);
            itemAsJsonObject = saveNmsItemStackMethod.invoke(nmsItemStackObj, nmsNbtTagCompoundObj);
        } catch (Throwable t) {
            return null;
        }
        return itemAsJsonObject.toString();
    }

    private JSONArray addColorToArray(String text) {
        JSONArray extraArr = new JSONArray();
        ChatColor color = ChatColor.WHITE;
        for (String part : text.split("(?=" + ChatColor.COLOR_CHAR + "[0-9a-fA-Fk-oK-ORr])")) {
            JSONObject objExtraTxt = new JSONObject();
            Matcher match = Pattern.compile("^" + ChatColor.COLOR_CHAR + "([0-9a-fA-Fk-oK-ORr]).*$").matcher(part);
            if (match.find()) {
                color = ChatColor.getByChar(match.group(1).charAt(0));
                if (part.length() == 2) continue;
            }
            objExtraTxt.put("text", ChatColor.stripColor(part));
            if (color.isColor()) {
                objExtraTxt.put("color", color.name().toLowerCase());
            }
            if (color.equals(ChatColor.RESET)) {
                objExtraTxt.put("color", "white");
            }
            if (color.isFormat()) {
                if (color.equals(ChatColor.MAGIC)) {
                    objExtraTxt.put("obfuscated", true);
                } else {
                    objExtraTxt.put(color.name().toLowerCase(), true);
                }
            }
            extraArr.add(objExtraTxt);
        }
        return extraArr;
    }

    public void setContructor(JSONArray array) {
        this.constructor = array;
    }

    @Override
    public UltimateFancy clone() {
        UltimateFancy newFanci = new UltimateFancy();
        newFanci.constructor = this.constructor;
        newFanci.pendentElements = this.pendentElements;
        newFanci.workingGroup = this.workingGroup;
        newFanci.lastformats = this.lastformats;
        return newFanci;
    }

    public class ExtraElement {
        private final String action;
        private final JSONObject json;

        public ExtraElement(String action, JSONObject json) {
            this.action = action;
            this.json = json;
        }

        public String getAction() {
            return this.action;
        }

        public JSONObject getJson() {
            return this.json;
        }
    }

}

class ReflectionUtil {
    private static String versionString;
    private static Map<String, Class<?>> loadedNMSClasses = new HashMap<>();
    private static Map<String, Class<?>> loadedOBCClasses = new HashMap<>();
    private static Map<Class<?>, Map<String, Method>> loadedMethods = new HashMap<>();

    public static String getVersion() {
        if (versionString == null) {
            String name = Bukkit.getServer().getClass().getPackage().getName();
            versionString = name.substring(name.lastIndexOf('.') + 1) + ".";
        }

        return versionString;
    }

    public static Class<?> getNMSClass(String nmsClassName) {
        if (loadedNMSClasses.containsKey(nmsClassName)) {
            return loadedNMSClasses.get(nmsClassName);
        }

        String clazzName = "net.minecraft.server." + getVersion() + nmsClassName;
        Class<?> clazz;

        try {
            clazz = Class.forName(clazzName);
        } catch (Throwable t) {
            t.printStackTrace();
            return loadedNMSClasses.put(nmsClassName, null);
        }

        loadedNMSClasses.put(nmsClassName, clazz);
        return clazz;
    }

    public static Class<?> getOBCClass(String obcClassName) {
        if (loadedOBCClasses.containsKey(obcClassName)) {
            return loadedOBCClasses.get(obcClassName);
        }

        String clazzName = "org.bukkit.craftbukkit." + getVersion() + obcClassName;
        Class<?> clazz;

        try {
            clazz = Class.forName(clazzName);
        } catch (Throwable t) {
            t.printStackTrace();
            loadedOBCClasses.put(obcClassName, null);
            return null;
        }

        loadedOBCClasses.put(obcClassName, clazz);
        return clazz;
    }

    public static Method getMethod(Class<?> clazz, String methodName, Class<?>... params) {
        if (!loadedMethods.containsKey(clazz)) {
            loadedMethods.put(clazz, new HashMap<>());
        }

        Map<String, Method> methods = loadedMethods.get(clazz);

        if (methods.containsKey(methodName)) {
            return methods.get(methodName);
        }

        try {
            Method method = clazz.getMethod(methodName, params);
            methods.put(methodName, method);
            loadedMethods.put(clazz, methods);
            return method;
        } catch (Exception e) {
            e.printStackTrace();
            methods.put(methodName, null);
            loadedMethods.put(clazz, methods);
            return null;
        }
    }
}

