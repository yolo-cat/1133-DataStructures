import java.util.*;

public class Expressions {

    // 運算符優先級定義
    private static int getPrecedence(char operator) {
        switch (operator) {
            case '~': return 5;          // 一元負號（最高優先級）
            case '^': return 4;          // 指數運算
            case '*':
            case '/':
            case '%': return 3;          // 乘除運算
            case '+':
            case '-': return 2;          // 加減運算 (二元)
            case '(': return 1;          // 左括號
            case ')': return 1;          // 右括號
            default: return 0;           // 運算元或未知符號
        }
    }

    // 檢查是否為右結合運算符
    private static boolean isRightAssociative(char operator) {
        return operator == '^' || operator == '~';  // 指數運算和一元負號都是右結合
    }

    // 檢查字符是否為運算符
    private static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '%' || c == '^';
    }

    // 檢查字符是否為變數或數字
    private static boolean isOperand(char c) {
        return Character.isLetterOrDigit(c);
    }

    // 檢查字符串是否為運算元
    private static boolean isOperand(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        return Character.isLetterOrDigit(token.charAt(0));
    }

    // 檢查是否為負號（而非減號）
    private static boolean isUnaryMinus(String expression, int index) {
        if (expression.charAt(index) != '-') {
            return false;
        }

        // 表達式開頭的負號
        if (index == 0) {
            return true;
        }

        // 檢查前一個字符
        char prevChar = expression.charAt(index - 1);
        // 在運算符或左括號後面的負號
        return isOperator(prevChar) || prevChar == '(';
    }

    /**
     * 將 Unicode 上標數字（如 ²、³、⁴...）轉換為 ^2、^3、^4 形式，方便堆疊時拆分
     */
    private static String replaceSuperscript(String expression) {
        Map<Character, String> superscriptMap = new HashMap<>();
        superscriptMap.put('²', "^2");
        superscriptMap.put('³', "^3");
        superscriptMap.put('⁴', "^4");
        superscriptMap.put('⁵', "^5");
        superscriptMap.put('⁶', "^6");
        superscriptMap.put('⁷', "^7");
        superscriptMap.put('⁸', "^8");
        superscriptMap.put('⁹', "^9");
        superscriptMap.put('¹', "^1");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (superscriptMap.containsKey(c)) {
                sb.append(superscriptMap.get(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    // 將一元負號與 (-x) 形式都標記為 ~，並移除 (-x) 的括號
    private static String markUnaryMinus(String expression) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            // 處理 (-x) 形式，保留括號，將負號標記為 ~
            if (c == '(' && i + 1 < expression.length() && expression.charAt(i+1) == '-') {
                sb.append('(').append('~');
                i += 1; // 跳過 (-，但保留括號結構
            } else if (c == '-' && isUnaryMinus(expression, i)) {
                sb.append('~');
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 預處理表達式：為負號和指數運算添加必要的括號
     * @param expression 原始表達式
     * @return 預處理後的表達式
     */
    private static String preprocessExpression(String expression) {
        if (expression == null || expression.isEmpty()) {
            return expression;
        }

        // 先將 Unicode 上標數字轉換為 ^數字
        expression = replaceSuperscript(expression);
        // 將一元負號標記為 ~
        expression = markUnaryMinus(expression);

        StringBuilder result = new StringBuilder();
        expression = expression.replaceAll("\\s+", ""); // 移除空白

        for (int i = 0; i < expression.length(); i++) {
            char current = expression.charAt(i);

            // 處理負號：在負號和其運算元外加括號
            if (current == '-' && isUnaryMinus(expression, i)) {
                result.append("(");
                result.append(current);

                // 找到負號後的運算元
                int j = i + 1;
                if (j < expression.length()) {
                    if (expression.charAt(j) == '(') {
                        // 負號後面是括號表達式，找到匹配的右括號
                        int bracketCount = 1;
                        result.append(expression.charAt(j));
                        j++;
                        while (j < expression.length() && bracketCount > 0) {
                            char c = expression.charAt(j);
                            result.append(c);
                            if (c == '(') bracketCount++;
                            else if (c == ')') bracketCount--;
                            j++;
                        }
                    } else {
                        // 負號後面是單個運算元
                        while (j < expression.length() && isOperand(expression.charAt(j))) {
                            result.append(expression.charAt(j));
                            j++;
                        }
                        // 檢查是否有指數運算
                        if (j < expression.length() && expression.charAt(j) == '^') {
                            result.append(expression.charAt(j));
                            j++;
                        }
                    }
                }
                result.append(")");
                i = j - 1; // 更新索引位置
            }
            // 處理指數運算：為指數運算的運算元加括號
            else if (current == '^') {
                // 指數符號前面應該已經有運算元，檢查是否需要加括號
                if (i > 0) {
                    char prev = expression.charAt(i - 1);
                    if (prev == ')') {
                        // 前面已經是括號表達式，直接添加指數
                        result.append(current);
                    } else if (isOperand(prev)) {
                        // 前面是單個運算元，需要為其加括號
                        // 回退找到完整的運算元
                        int start = i - 1;
                        while (start > 0 && isOperand(expression.charAt(start - 1))) {
                            start--;
                        }

                        // 重新構建這部分，加上括號
                        String operand = expression.substring(start, i);
                        // 移除已添加的運算元
                        result.setLength(result.length() - operand.length());
                        result.append("(").append(operand).append(")");
                        result.append(current);
                    } else {
                        result.append(current);
                    }
                } else {
                    result.append(current);
                }
            }
            else {
                result.append(current);
            }
        }

        return result.toString();
    }

    /**
     * 中序表達式轉換為後序表達式 (Shunting Yard Algorithm)
     * @param infix 中序表達式
     * @return 後序表達式
     */
    public static String infixToPostfix(String infix) {
        if (infix == null || infix.isEmpty()) {
            return "";
        }

        String preprocessed = preprocessExpression(infix);
        StringBuilder result = new StringBuilder();
        Stack<String> operatorStack = new Stack<>();
        preprocessed = preprocessed.replaceAll("\\s+", "");

        for (int i = 0; i < preprocessed.length(); ) {
            char current = preprocessed.charAt(i);

            // 處理運算元
            if (isOperand(current)) {
                result.append(current).append(" ");
                i++;
            }
            // 處理一元負號 ~
            else if (current == '~') {
                operatorStack.push("~");
                i++;
            }
            // 處理左括號
            else if (current == '(') {
                operatorStack.push("(");
                i++;
            }
            // 處理右括號
            else if (current == ')') {
                while (!operatorStack.isEmpty() && !operatorStack.peek().equals("(")) {
                    result.append(operatorStack.pop()).append(" ");
                }
                if (!operatorStack.isEmpty()) operatorStack.pop();
                i++;
            }
            // 處理運算符
            else if (isOperator(current)) {
                String op = String.valueOf(current);
                while (!operatorStack.isEmpty() && !operatorStack.peek().equals("(") &&
                        ((getPrecedence(operatorStack.peek().charAt(0)) > getPrecedence(current)) ||
                        (getPrecedence(operatorStack.peek().charAt(0)) == getPrecedence(current) && !isRightAssociative(current)))) {
                    result.append(operatorStack.pop()).append(" ");
                }
                operatorStack.push(op);
                i++;
            } else {
                i++;
            }
        }

        while (!operatorStack.isEmpty()) {
            result.append(operatorStack.pop()).append(" ");
        }
        return result.toString().trim();
    }

    /**
     * 中序表達式轉換為前序表達式
     * @param infix 中序表達式
     * @return 前序表達式
     */
    public static String infixToPrefix(String infix) {
        if (infix == null || infix.isEmpty()) {
            return "";
        }
        String preprocessed = preprocessExpression(infix);
        String reversedInfix = reverseExpression(preprocessed);
        String reversedPostfix = infixToPostfixForPrefix(reversedInfix);
        return reverseTokens(reversedPostfix);
    }

    // 修改：前序用的後序轉換也要支援一元負號 ~
    private static String infixToPostfixForPrefix(String infix) {
        if (infix == null || infix.isEmpty()) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        Stack<String> operatorStack = new Stack<>();
        infix = infix.replaceAll("\\s+", "");

        for (int i = 0; i < infix.length(); ) {
            char current = infix.charAt(i);

            if (isOperand(current)) {
                result.append(current).append(" ");
                i++;
            }
            // 處理一元負號 ~
            else if (current == '~') {
                operatorStack.push("~");
                i++;
            }
            else if (current == '(') {
                operatorStack.push("(");
                i++;
            } else if (current == ')') {
                while (!operatorStack.isEmpty() && !operatorStack.peek().equals("(")) {
                    result.append(operatorStack.pop()).append(" ");
                }
                if (!operatorStack.isEmpty()) operatorStack.pop();
                i++;
            } else if (isOperator(current)) {
                String op = String.valueOf(current);
                // 修改：為前序轉換調整運算符比較邏輯
                while (!operatorStack.isEmpty() && !operatorStack.peek().equals("(") &&
                        ((getPrecedence(operatorStack.peek().charAt(0)) > getPrecedence(current)) ||
                        (getPrecedence(operatorStack.peek().charAt(0)) == getPrecedence(current) && isRightAssociative(current)))) {
                    result.append(operatorStack.pop()).append(" ");
                }
                operatorStack.push(op);
                i++;
            } else {
                i++;
            }
        }

        while (!operatorStack.isEmpty()) {
            result.append(operatorStack.pop()).append(" ");
        }
        return result.toString().trim();
    }

    // 反轉表達式並交換括號
    private static String reverseExpression(String expression) {
        StringBuilder reversed = new StringBuilder();

        for (int i = expression.length() - 1; i >= 0; i--) {
            char c = expression.charAt(i);
            if (c == '(') {
                reversed.append(')');
            } else if (c == ')') {
                reversed.append('(');
            } else {
                reversed.append(c);
            }
        }

        return reversed.toString();
    }

    // 反轉標記化的表達式（用空格分隔的標記）
    private static String reverseTokens(String expression) {
        if (expression.isEmpty()) {
            return "";
        }
        String[] tokens = expression.split("\\s+");
        StringBuilder result = new StringBuilder();
        for (int i = tokens.length - 1; i >= 0; i--) {
            // 將 ~ 轉回 - 以符合題目格式
            if (tokens[i].equals("~")) {
                result.append("-");
            } else {
                result.append(tokens[i]);
            }
            if (i > 0) {
                result.append(" ");
            }
        }
        return result.toString();
    }

    /**
     * 移除後序/前序結果中的括號包裹次方的括號 (如 (^2) -> ^2)，以及將 ~ 轉回 -
     */
    private static String removePowerParentheses(String expr) {
        // 將所有 ~ 統一轉成 -，不區分一元或二元
        return expr.replaceAll("\\(\\^(\\d)\\)", "^$1").replace("~", "-");
    }

    /**
     * 格式化輸出結果
     * @param infix 中序表達式
     * @return 格式化的轉換結果
     */
    public static String convertExpression(String infix) {
        try {
            String postfix = removePowerParentheses(infixToPostfix(infix));
            String prefix = removePowerParentheses(infixToPrefix(infix));
            StringBuilder result = new StringBuilder();
            result.append("中序表達式: ").append(infix).append("\n");
            result.append("後序表達式: ").append(postfix).append("\n");
            result.append("前序表達式: ").append(prefix).append("\n");
            return result.toString();
        } catch (Exception e) {
            return "轉換錯誤: " + e.getMessage();
        }
    }

    // 測試方法
    public static void main(String[] args) {
        // 測試案例
        String[] testCases = {
            "a+b",
            "-a",
            "-a+b",
            "(a+b-c)²*c",  // 改用^符號
            "-a*b-c-(-b)",
            "a^2^3",        // 測試指數運算右結合性
            "-a^2"          // 測試負號優先級高於指數
        };

        System.out.println("=== 表達式轉換測試 ===\n");

        for (String testCase : testCases) {
            System.out.println(convertExpression(testCase));
            System.out.println("---");
        }
    }
}
