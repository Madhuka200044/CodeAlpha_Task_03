import java.util.*;
import java.util.regex.*;
import java.time.*;
import java.io.*;

public class IntelligentChatbot 
{
	private static final Map<String, String[]> KNOWLEDGE_BASE = new HashMap<>();
    private static final Map<String, String> RESPONSE_PATTERNS = new HashMap<>();
    private static final Set<String> GREETINGS = new HashSet<>();
    private static final Set<String> FAREWELLS = new HashSet<>();
    private static final String MODEL_FILE = "chatbot_model.ser";
    private static Map<String, Map<String, Integer>> learningModel;
    
    static 
	{
        // Initialize knowledge base
        KNOWLEDGE_BASE.put("weather", new String[]
		{
            "The weather is nice today!", 
            "It looks like it might rain.", 
            "Sunny and warm - perfect day!"
        });
        
        KNOWLEDGE_BASE.put("time", new String[]
		{
            "The current time is " + LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")),
            "My clock shows " + LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("h:mm a"))
        });
        
        KNOWLEDGE_BASE.put("date", new String[]
		{
            "Today is " + LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("MMMM d, yyyy")),
            "The date is " + LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("MM/dd/yyyy"))
        });
        
        // Initialize response patterns
        RESPONSE_PATTERNS.put("(.*)(hello|hi|hey)(.*)", "greeting");
        RESPONSE_PATTERNS.put("(.*)(how are you|how's it going)(.*)", "how_are_you");
        RESPONSE_PATTERNS.put("(.*)(your name|who are you)(.*)", "identity");
        RESPONSE_PATTERNS.put("(.*)(weather|temperature|forecast)(.*)", "weather");
        RESPONSE_PATTERNS.put("(.*)(time|clock)(.*)", "time");
        RESPONSE_PATTERNS.put("(.*)(date|day|today)(.*)", "date");
        RESPONSE_PATTERNS.put("(.*)(bye|goodbye|see ya)(.*)", "farewell");
        
        // Initialize greetings and farewells
        GREETINGS.addAll(Arrays.asList("Hello!", "Hi there!", "Greetings!", "Nice to see you!"));
        FAREWELLS.addAll(Arrays.asList("Goodbye!", "See you later!", "Have a nice day!", "Bye bye!"));
        
        // Load or initialize learning model
        learningModel = loadModel();
        if (learningModel == null) 
		{
            learningModel = new HashMap<>();
        }
    }
	public static void main(String[] args)
	{
		 Scanner scanner = new Scanner(System.in);
        System.out.println("Intelligent Chatbot: Hello! How can I assist you today? (Type 'quit' to exit)");
		
		while (true) {
            System.out.print("You: ");
            String input = scanner.nextLine().toLowerCase();
            
            if (input.equals("quit")) {
                saveModel();
                System.out.println("Chatbot: Goodbye! Have a great day!");
                break;
            }
            
            String response = generateResponse(input);
            System.out.println("Chatbot: " + response);
            
            // Learn from interaction
            updateLearningModel(input, response);
        }
        
        scanner.close();
	}
	
	 private static String generateResponse(String input) 
	 {
        // Check for exact matches in learning model first
        if (learningModel.containsKey(input)) 
		{
            Map<String, Integer> responseCounts = learningModel.get(input);
            String bestResponse = Collections.max(responseCounts.entrySet(), Map.Entry.comparingByValue()).getKey();
            return bestResponse;
        }
		
		// Pattern matching
        for (Map.Entry<String, String> entry : RESPONSE_PATTERNS.entrySet()) 
		{
            Pattern pattern = Pattern.compile(entry.getKey());
            Matcher matcher = pattern.matcher(input);
			
			if (matcher.matches()) 
			{
                String responseType = entry.getValue();
                
                switch (responseType) 
				{
                    case "greeting":
                        return getRandomResponse(GREETINGS);
                    case "how_are_you":
                        return "I'm just a chatbot, but I'm functioning well! How about you?";
                    case "identity":
                        return "I'm an intelligent chatbot created to assist you!";
                    case "weather":
                        return getRandomResponse(KNOWLEDGE_BASE.get("weather"));
                    case "time":
                        return getRandomResponse(KNOWLEDGE_BASE.get("time"));
                    case "date":
                        return getRandomResponse(KNOWLEDGE_BASE.get("date"));
                    case "farewell":
                        return getRandomResponse(FAREWELLS);
                }
            }
        }
		// Default response if no patterns match
        return "I'm not sure I understand. Could you rephrase that?";
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
    
    private static void updateLearningModel(String input, String response) 
	{
        learningModel.putIfAbsent(input, new HashMap<>());
        Map<String, Integer> responseCounts = learningModel.get(input);
        responseCounts.put(response, responseCounts.getOrDefault(response, 0) + 1);
    }
    
    private static void saveModel() 
	{
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(MODEL_FILE))) 
		{
            oos.writeObject(learningModel);
        } 
		catch (IOException e) 
		{
            System.err.println("Failed to save learning model: " + e.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    private static Map<String, Map<String, Integer>> loadModel() 
	{
        File file = new File(MODEL_FILE);
        if (file.exists()) 
		{
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) 
			{
                return (Map<String, Map<String, Integer>>) ois.readObject();
            }
			catch (IOException | ClassNotFoundException e) 
			{
                System.err.println("Failed to load learning model: " + e.getMessage());
            }
        }
        return null;
    }
}
            
