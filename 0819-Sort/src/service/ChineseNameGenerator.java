package service;

import java.util.Random;

public class ChineseNameGenerator {
    private static final String[] PREFIXES = {"台", "中", "華", "國", "金", "大", "新", "正", "和", "信"};
    private static final String[] INDUSTRIES = {"鋼", "電", "科", "建", "能", "材", "化", "機", "光", "網"};
    private static final String[] SUFFIXES = {"股份", "公司", "企業", "集團", "工業", "科技"};
    private static final Random RANDOM = new Random();

    public String generateName() {
        int length = 2 + RANDOM.nextInt(5); // 2 to 6 characters
        StringBuilder name = new StringBuilder();

        // Ensure the generated name has the desired length
        while (name.length() < length) {
            int type = RANDOM.nextInt(3);
            if (type == 0 && name.length() + PREFIXES[0].length() <= length) {
                name.append(PREFIXES[RANDOM.nextInt(PREFIXES.length)]);
            }
            if (type == 1 && name.length() + INDUSTRIES[0].length() <= length) {
                name.append(INDUSTRIES[RANDOM.nextInt(INDUSTRIES.length)]);
            }
            if (type == 2 && name.length() + SUFFIXES[0].length() <= length) {
                name.append(SUFFIXES[RANDOM.nextInt(SUFFIXES.length)]);
            }
        }

        return name.toString();
    }
}
