import java.util.*;
import java.util.regex.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.io.*;
import java.nio.file.*;

public class IntelligentChatbot 
{
    private static final Map<String, String[]> KNOWLEDGE_BASE = new HashMap<>();
    private static final Map<String, PatternPriority> RESPONSE_PATTERNS = new LinkedHashMap<>();
    private static final Set<String> GREETINGS = new HashSet<>();
    private static final Set<String> FAREWELLS = new HashSet<>();
    private static final String MODEL_FILE = "chatbot_model.ser";
    private static Map<String, Map<String, Integer>> learningModel;
    private static final Object modelLock = new Object();
    
    static class PatternPriority 
	{
        Pattern pattern;
        int priority;
        
        PatternPriority(String regex, int priority) 
		{
            this.pattern = Pattern.compile(regex);
            this.priority = priority;
        }
    }
    
    static 
	{
        initializeKnowledgeBase();
        initializeResponsePatterns();
        initializeGreetingsAndFarewells();
        loadLearningModel();
    }

    private static void initializeKnowledgeBase() 
	{
        KNOWLEDGE_BASE.put("weather", new String[]
		{
            "The weather is nice today!", 
            "It looks like it might rain.", 
            "Sunny and warm - perfect day!"
        });
        
        KNOWLEDGE_BASE.put("time", new String[]
		{
            "The current time is " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")),
            "My clock shows " + LocalTime.now().format(DateTimeFormatter.ofPattern("h:mm a"))
        });
        
        KNOWLEDGE_BASE.put("date", new String[]
		{
            "Today is " + LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM d, yyyy")),
            "The date is " + LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))
        });
        
        KNOWLEDGE_BASE.put("joke", new String[]
		{
            "Why don't scientists trust atoms? Because they make up everything!",
            "Did you hear about the mathematician who's afraid of negative numbers? He'll stop at nothing to avoid them!"
        });
    }

    private static void initializeResponsePatterns() 
	{
        RESPONSE_PATTERNS.put("farewell", new PatternPriority("(.*)(bye|goodbye|see ya)(.*)", 3));
        RESPONSE_PATTERNS.put("greeting", new PatternPriority("(.*)(hello|hi|hey)(.*)", 3));
        RESPONSE_PATTERNS.put("how_are_you", new PatternPriority("(.*)(how are you|how's it going)(.*)", 2));
        RESPONSE_PATTERNS.put("identity", new PatternPriority("(.*)(your name|who are you)(.*)", 2));
        RESPONSE_PATTERNS.put("weather", new PatternPriority("(.*)(weather|temperature|forecast)(.*)", 1));
        RESPONSE_PATTERNS.put("time", new PatternPriority("(.*)(time|clock)(.*)", 1));
        RESPONSE_PATTERNS.put("date", new PatternPriority("(.*)(date|day|today)(.*)", 1));
        RESPONSE_PATTERNS.put("joke", new PatternPriority("(.*)(joke|funny)(.*)", 1));
    }

    private static void initializeGreetingsAndFarewells() 
	{
        GREETINGS.addAll(Arrays.asList("Hello!", "Hi there!", "Greetings!", "Nice to see you!"));
        FAREWELLS.addAll(Arrays.asList("Goodbye!", "See you later!", "Have a nice day!", "Bye bye!"));
    }

    @SuppressWarnings("unchecked")
    private static void loadLearningModel() 
	{
        synchronized(modelLock) 
		{
            File file = new File(MODEL_FILE);
            if (file.exists()) 
			{
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) 
				{
                    learningModel = (Map<String, Map<String, Integer>>) ois.readObject();
                } catch (IOException | ClassNotFoundException e) 
				{
                    System.err.println("Failed to load learning model: " + e.getMessage());
                    learningModel = new HashMap<>();
                }
            } 
			else 
			{
                learningModel = new HashMap<>();
            }
        }
    }

    public static void main(String[] args) 
	{
        Scanner scanner = new Scanner(System.in);
        System.out.println("Intelligent Chatbot v2.0: Hello! How can I assist you today? (Type 'quit' to exit)");
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> 
		{
            System.out.println("\nSaving learning model before exiting...");
            saveModel();
        }));

        while (true) 
		{
            System.out.print("You: ");
            String input = scanner.nextLine().trim();
            
            if (input.equalsIgnoreCase("quit")) 
			{
                break;
            }
            
            try 
			{
                String response = generateResponse(input);
                System.out.println("Chatbot: " + response);
                updateLearningModel(input, response);
            } 
			catch (Exception e) 
			{
                System.err.println("Error processing input: " + e.getMessage());
                System.out.println("Chatbot: I encountered an error. Could you try again?");
            }
        }
        
        scanner.close();
        saveModel();
        System.out.println("Chatbot: Goodbye! Have a great day!");
    }

    private static String generateResponse(String input) 
	{
        String sentiment = analyzeSentiment(input);
        String lowerInput = input.toLowerCase();

        synchronized(modelLock) 
		{
            if (learningModel.containsKey(lowerInput)) 
			{
                Map<String, Integer> responseCounts = learningModel.get(lowerInput);
                String bestResponse = Collections.max(responseCounts.entrySet(), Map.Entry.comparingByValue()).getKey();
                return adjustForSentiment(bestResponse, sentiment);
            }
        }

        for (Map.Entry<String, PatternPriority> entry : RESPONSE_PATTERNS.entrySet()) 
		{
            Matcher matcher = entry.getValue().pattern.matcher(lowerInput);
            if (matcher.matches()) 
			{
                String responseType = entry.getKey();
                String response;
                
                switch (responseType) 
				{
                    case "greeting":
                        response = getRandomResponse(GREETINGS);
                        break;
                    case "how_are_you":
                        response = "I'm just a chatbot, but I'm functioning well! How about you?";
                        break;
                    case "identity":
                        response = "I'm an intelligent chatbot created to assist you!";
                        break;
                    case "weather":
                        response = getRandomArrayResponse(KNOWLEDGE_BASE.get("weather"));
                        break;
                    case "time":
                        response = getRandomArrayResponse(KNOWLEDGE_BASE.get("time"));
                        break;
                    case "date":
                        response = getRandomArrayResponse(KNOWLEDGE_BASE.get("date"));
                        break;
                    case "joke":
                        response = getRandomArrayResponse(KNOWLEDGE_BASE.get("joke"));
                        break;
                    case "farewell":
                        response = getRandomResponse(FAREWELLS);
                        break;
                    default:
                        response = "I'm not sure I understand. Could you rephrase that?";
                }
                
                return adjustForSentiment(response, sentiment);
            }
        }

        return adjustForSentiment("I'm not sure I understand. Could you rephrase that?", sentiment);
    }

    private static String getRandomResponse(Collection<String> responses) 
	{
        if (responses == null || responses.isEmpty()) 
		{
            return "I don't have a response for that.";
        }
        int index = new Random().nextInt(responses.size());
        return responses.toArray(new String[0])[index];
    }

    private static String getRandomArrayResponse(String[] responses) 
	{
        if (responses == null || responses.length == 0) 
		{
            return "I don't have a response for that.";
        }
        int index = new Random().nextInt(responses.length);
        return responses[index];
    }

    private static String analyzeSentiment(String input) 
	{
        if (input.matches(".*\\b(awesome|great|happy|love|wonderful)\\b.*")) 
		{
            return "positive";
        } 
		else if (input.matches(".*\\b(bad|terrible|awful|hate|sad)\\b.*")) 
		{
            return "negative";
        }
        return "neutral";
    }

    private static String adjustForSentiment(String response, String sentiment) 
	{
        if (sentiment.equals("positive")) 
		{
            return response + " That's wonderful to hear!";
        } 
		else if (sentiment.equals("negative")) 
		{
            return response + " I'm sorry to hear that. Is there anything I can do to help?";
        }
        return response;
    }

    private static void updateLearningModel(String input, String response) 
	{
        synchronized(modelLock) 
		{
            learningModel.putIfAbsent(input.toLowerCase(), new HashMap<>());
            Map<String, Integer> responseCounts = learningModel.get(input.toLowerCase());
            responseCounts.replaceAll((k, v) -> (int)(v * 0.95));
            responseCounts.put(response, responseCounts.getOrDefault(response, 0) + 1);
        }
    }

    private static void saveModel() 
	{
        synchronized(modelLock) 
		{
            try 
			{
                Path directory = Paths.get(MODEL_FILE).getParent();
                if (directory != null) 
				{
                    Files.createDirectories(directory);
                }
                
                try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(MODEL_FILE))) 
				{
                    oos.writeObject(learningModel);
                }
            } 
			catch (IOException e) 
			{
                System.err.println("Failed to save learning model: " + e.getMessage());
            }
        }
    }
}