import java.util.*;

public class EmplyeePerformaceEvaluation {

    // The knowledge base contains the rules for employee evaluation
    static class KnowledgeBase {
        private List<Rule> rules;
        private List<SpecialRule> specialRules;

        public KnowledgeBase() {
            rules = new ArrayList<>();
            specialRules = new ArrayList<>();
        }

        public void addRule(Rule rule) {
            rules.add(rule);
        }

        public void addSpecialRule(SpecialRule specialRule) {
            specialRules.add(specialRule);
        }

        public String evaluate(Map<String, Object> facts) {
            // Check for special cases (like firing or giving a raise)
            for (SpecialRule specialRule : specialRules) {
                if (specialRule.evaluate(facts)) {
                    return specialRule.getOutcome(); // Outcome if special rule is met
                }
            }

            // Check standard rules for basic evaluation
            for (Rule rule : rules) {
                if (!rule.evaluate(facts)) {
                    return rule.getOutcome(); // Outcome if rule fails
                }
            }

            return "Exceeds Expectations"; // Default outcome if all rules are met and no special case applies
        }
    }

    // A rule with a condition, expected value, and an outcome if condition is not met
    static class Rule {
        private String condition;
        private Object expectedValue;
        private String outcome;

        public Rule(String condition, Object expectedValue, String outcome) {
            this.condition = condition;
            this.expectedValue = expectedValue;
            this.outcome = outcome;
        }	

        public boolean evaluate(Map<String, Object> facts) {
            if (!facts.containsKey(condition)) {
                return false; // Condition key not found
            }

            Object factValue = facts.get(condition);
            if (factValue instanceof Integer) {
                return (Integer) factValue >= (Integer) expectedValue; // Compare integers
            } else {
                return factValue.equals(expectedValue); // Check for equality with expected value
            }
        }

        public String getOutcome() {
            return outcome;
        }
    }

    // Special rule for complex conditions with a specific outcome if condition is met
    static class SpecialRule {
        private String[] conditions;
        private Object expectedValue;
        private int productivityThreshold;
        private String outcome;

        public SpecialRule(String[] conditions, Object expectedValue, int productivityThreshold, String outcome) {
            this.conditions = conditions;
            this.expectedValue = expectedValue;
            this.productivityThreshold = productivityThreshold;
            this.outcome = outcome;
        }

        public boolean evaluate(Map<String, Object> facts) {
            boolean allConditionsMet = true;

            for (String condition : conditions) {
                if (!facts.containsKey(condition) || !facts.get(condition).equals(expectedValue)) {
                    allConditionsMet = false;
                    break;
                }
            }

            // Special rule for firing: all conditions must be "Poor" and productivity < 30
            if (allConditionsMet && facts.containsKey("productivity")) {
                int productivity = (Integer) facts.get("productivity");
                if (productivity < productivityThreshold) {
                    return true; // Condition for firing
                }
            }

            // Special rule for raise: at least one condition "Excellent" and productivity > 70
            if (facts.containsKey("productivity") && (Integer) facts.get("productivity") > productivityThreshold) {
                boolean anyConditionExcellent = Arrays.stream(conditions).anyMatch(condition ->
                    facts.get(condition).equals("Excellent")
                );

                if (anyConditionExcellent) {
                    return true; // Condition for a raise
                }
            }

            return false; // If no special condition is met
        }

        public String getOutcome() {
            return outcome;
        }
    }

    // Main method to interact with the expert system
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Build the knowledge base with standard rules for employee evaluation
        KnowledgeBase knowledgeBase = new KnowledgeBase();
        knowledgeBase.addRule(new Rule("productivity", 70, "Needs Improvement")); // Minimum productivity
        knowledgeBase.addRule(new Rule("teamwork", "Good", "Needs Improvement")); // Teamwork must be "Good"
        knowledgeBase.addRule(new Rule("communication", "Adequate", "Needs Improvement")); // Communication must be at least "Adequate"

        // Add a special rule for raise.
        knowledgeBase.addSpecialRule(new SpecialRule(new String[]{"teamwork", "communication"}, "Excellent", 70, "Gets a Raise"));

        // Add a special rule for firing.
        knowledgeBase.addSpecialRule(new SpecialRule(new String[]{"teamwork", "communication"}, "Poor", 30, "Fired"));

        // Collect employee performance metrics
        System.out.print("Enter employee productivity (0-100): ");
        int productivity = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        System.out.print("Enter employee teamwork level (Poor, Good, Excellent): ");
        String teamwork = scanner.nextLine();

        System.out.print("Enter employee communication level (Poor, Adequate, Excellent): ");
        String communication = scanner.nextLine();

        // Store employee performance metrics as facts
        Map<String, Object> facts = new HashMap<>();
        facts.put("productivity", productivity);
        facts.put("teamwork", teamwork);
        facts.put("communication", communication);

        // Evaluate the employee based on the knowledge base
        String result = knowledgeBase.evaluate(facts);

        // Provide an evaluation outcome
        System.out.println("Employee evaluation result: " + result);

        scanner.close(); // Close the scanner to prevent resource leaks
    }
}
