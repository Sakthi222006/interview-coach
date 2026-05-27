package com.interviewcoach.backend.config;

import com.interviewcoach.backend.model.Question;
import com.interviewcoach.backend.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
// CommandLineRunner: Spring calls run() automatically after the app starts
public class DataInitializer implements CommandLineRunner {

    private final QuestionRepository questionRepository;

    @Override
    public void run(String... args) {
        // Only seed if table is empty — safe to restart without duplicates
        if (questionRepository.count() > 0) {
            log.info("Questions already seeded. Skipping.");
            return;
        }

        log.info("Seeding question bank...");
        questionRepository.saveAll(buildQuestions());
        log.info("Seeded {} questions.", questionRepository.count());
    }

    private List<Question> buildQuestions() {
        return List.of(

            // ── DSA — EASY ──────────────────────────────────────────
            Question.builder()
                .topic("DSA").difficulty("EASY").questionType("MCQ")
                .questionText("What is the time complexity of accessing an element in an array by index?")
                .optionA("O(n)").optionB("O(log n)").optionC("O(1)").optionD("O(n²)")
                .correctAnswer("C")
                .explanation("Array elements are stored in contiguous memory locations. Given a base address and index, the CPU directly calculates the memory address in constant time: address = base + (index × element_size).")
                .tags("arrays,complexity")
                .build(),

            Question.builder()
                .topic("DSA").difficulty("EASY").questionType("MCQ")
                .questionText("Which data structure uses LIFO (Last In, First Out) ordering?")
                .optionA("Queue").optionB("Stack").optionC("Linked List").optionD("Tree")
                .correctAnswer("B")
                .explanation("A Stack follows LIFO: the last element pushed is the first to be popped. Think of a stack of plates — you always take from the top. Common uses: function call stack, undo/redo, bracket matching.")
                .tags("stack,data-structures")
                .build(),

            Question.builder()
                .topic("DSA").difficulty("EASY").questionType("MCQ")
                .questionText("What is the worst-case time complexity of Binary Search?")
                .optionA("O(n)").optionB("O(n log n)").optionC("O(log n)").optionD("O(1)")
                .correctAnswer("C")
                .explanation("Binary Search halves the search space on every step. Starting with n elements: n → n/2 → n/4 → ... → 1. The number of steps is log₂(n), giving O(log n) worst case. Requires a sorted array.")
                .tags("binary-search,searching,arrays")
                .build(),

            Question.builder()
                .topic("DSA").difficulty("EASY").questionType("MCQ")
                .questionText("Which of the following is NOT a linear data structure?")
                .optionA("Array").optionB("Linked List").optionC("Binary Tree").optionD("Queue")
                .correctAnswer("C")
                .explanation("Linear data structures store elements sequentially (each element has at most one predecessor and one successor). A Binary Tree is non-linear — each node can have multiple children, creating a hierarchical structure.")
                .tags("data-structures,trees")
                .build(),

            Question.builder()
                .topic("DSA").difficulty("EASY").questionType("MCQ")
                .questionText("What does a Queue's 'enqueue' operation do?")
                .optionA("Removes from front").optionB("Adds to front").optionC("Adds to rear").optionD("Removes from rear")
                .correctAnswer("C")
                .explanation("A Queue follows FIFO (First In, First Out). Enqueue adds elements to the rear (back) of the queue. Dequeue removes from the front. Think of a line at a store: new people join at the back, service happens at the front.")
                .tags("queue,data-structures")
                .build(),

            // ── DSA — MEDIUM ─────────────────────────────────────────
            Question.builder()
                .topic("DSA").difficulty("MEDIUM").questionType("MCQ")
                .questionText("What is the average time complexity of inserting an element into a Hash Map?")
                .optionA("O(n)").optionB("O(log n)").optionC("O(n log n)").optionD("O(1)")
                .correctAnswer("D")
                .explanation("A well-designed hash map computes the bucket index using a hash function in O(1). With a good hash function and low load factor, collisions are rare, making insertions O(1) average. Worst case is O(n) when all keys collide.")
                .tags("hashmap,hashing,complexity")
                .build(),

            Question.builder()
                .topic("DSA").difficulty("MEDIUM").questionType("MCQ")
                .questionText("In a min-heap, which element is always at the root?")
                .optionA("Maximum element").optionB("Minimum element").optionC("Random element").optionD("Last inserted element")
                .correctAnswer("B")
                .explanation("A min-heap satisfies the heap property: every parent is smaller than or equal to its children. This guarantees the minimum element is always at the root (index 0 in array representation). Extraction is O(log n).")
                .tags("heap,priority-queue,tree")
                .build(),

            Question.builder()
                .topic("DSA").difficulty("MEDIUM").questionType("MCQ")
                .questionText("Which traversal of a BST gives elements in sorted order?")
                .optionA("Preorder").optionB("Postorder").optionC("Level-order").optionD("Inorder")
                .correctAnswer("D")
                .explanation("Inorder traversal (Left → Root → Right) of a BST visits nodes in ascending sorted order because of the BST property: left subtree contains only smaller values, right subtree only larger values.")
                .tags("bst,tree,traversal")
                .build(),

            Question.builder()
                .topic("DSA").difficulty("MEDIUM").questionType("MCQ")
                .questionText("What algorithm is typically used to find the shortest path in an unweighted graph?")
                .optionA("DFS").optionB("BFS").optionC("Dijkstra").optionD("Bellman-Ford")
                .correctAnswer("B")
                .explanation("BFS (Breadth-First Search) explores nodes level by level, guaranteeing the first time it reaches a node is via the shortest path (fewest edges). Dijkstra handles weighted graphs; BFS is optimal for unweighted graphs.")
                .tags("graph,bfs,shortest-path")
                .build(),

            // ── DSA — HARD ────────────────────────────────────────────
            Question.builder()
                .topic("DSA").difficulty("HARD").questionType("MCQ")
                .questionText("What is the time complexity of Dijkstra's algorithm using a binary min-heap?")
                .optionA("O(V²)").optionB("O(E log V)").optionC("O(V log E)").optionD("O(E + V)")
                .correctAnswer("B")
                .explanation("With a binary min-heap: each edge causes at most one decrease-key operation (O(log V)), and there are E edges total, giving O(E log V). Using a Fibonacci heap reduces this to O(E + V log V).")
                .tags("dijkstra,graph,complexity,heap")
                .build(),

            // ── JAVA — EASY ───────────────────────────────────────────
            Question.builder()
                .topic("JAVA").difficulty("EASY").questionType("MCQ")
                .questionText("Which keyword is used to prevent a class from being subclassed in Java?")
                .optionA("static").optionB("abstract").optionC("final").optionD("private")
                .correctAnswer("C")
                .explanation("The 'final' keyword on a class prevents inheritance. Example: public final class String {...}. The String class is final to ensure immutability and security. 'abstract' is the opposite — it forces subclassing.")
                .tags("java,oop,keywords")
                .build(),

            Question.builder()
                .topic("JAVA").difficulty("EASY").questionType("MCQ")
                .questionText("What is the default value of an int variable in Java (as a class field)?")
                .optionA("null").optionB("undefined").optionC("1").optionD("0")
                .correctAnswer("D")
                .explanation("Java automatically initializes class-level (instance/static) fields. Numeric types default to 0, boolean to false, and object references to null. Note: local variables inside methods do NOT get defaults and must be explicitly initialized.")
                .tags("java,primitives,initialization")
                .build(),

            Question.builder()
                .topic("JAVA").difficulty("EASY").questionType("MCQ")
                .questionText("Which collection interface does ArrayList implement?")
                .optionA("Set").optionB("Map").optionC("Queue").optionD("List")
                .correctAnswer("D")
                .explanation("ArrayList implements the List interface, which extends Collection. List allows duplicate elements and maintains insertion order. ArrayList is backed by a resizable array, giving O(1) random access but O(n) insertion at arbitrary positions.")
                .tags("java,collections,arraylist")
                .build(),

            Question.builder()
                .topic("JAVA").difficulty("EASY").questionType("MCQ")
                .questionText("What does the 'static' keyword mean for a method?")
                .optionA("The method cannot be overridden").optionB("The method belongs to the class, not instances").optionC("The method runs only once").optionD("The method is thread-safe")
                .correctAnswer("B")
                .explanation("A static method belongs to the class itself, not to any particular instance. It can be called without creating an object: ClassName.method(). Static methods cannot access instance variables (non-static fields) directly.")
                .tags("java,oop,static")
                .build(),

            // ── JAVA — MEDIUM ─────────────────────────────────────────
            Question.builder()
                .topic("JAVA").difficulty("MEDIUM").questionType("MCQ")
                .questionText("What is the difference between '==' and '.equals()' for String comparison in Java?")
                .optionA("No difference").optionB("== compares references, .equals() compares content").optionC(".equals() compares references, == compares content").optionD("== only works for primitives")
                .correctAnswer("B")
                .explanation("== compares object references (memory addresses). Two String objects with the same content may be at different addresses, so == returns false. .equals() compares the actual character content. Exception: String literals from the pool may share references.")
                .tags("java,strings,equality")
                .build(),

            Question.builder()
                .topic("JAVA").difficulty("MEDIUM").questionType("MCQ")
                .questionText("Which of these is NOT a feature of Java 8 Streams?")
                .optionA("Lazy evaluation").optionB("Parallel processing").optionC("Mutating the source collection").optionD("Method chaining")
                .correctAnswer("C")
                .explanation("Streams do NOT mutate their source. They produce new streams/results. Key features: laziness (intermediate operations like filter/map don't execute until a terminal operation like collect/forEach is called), parallel support via .parallelStream(), and method chaining.")
                .tags("java,streams,java8,functional")
                .build(),

            Question.builder()
                .topic("JAVA").difficulty("MEDIUM").questionType("MCQ")
                .questionText("What is the purpose of the 'volatile' keyword in Java?")
                .optionA("Makes a variable constant").optionB("Ensures visibility of changes across threads").optionC("Makes a method synchronized").optionD("Prevents garbage collection")
                .correctAnswer("B")
                .explanation("'volatile' ensures that reads/writes to a variable go directly to main memory, not the CPU cache. This guarantees visibility: when one thread writes a volatile variable, other threads immediately see the updated value. It does NOT provide atomicity for compound operations.")
                .tags("java,concurrency,threading,volatile")
                .build(),

            // ── SQL — EASY ────────────────────────────────────────────
            Question.builder()
                .topic("SQL").difficulty("EASY").questionType("MCQ")
                .questionText("Which SQL clause is used to filter rows based on a condition?")
                .optionA("ORDER BY").optionB("GROUP BY").optionC("HAVING").optionD("WHERE")
                .correctAnswer("D")
                .explanation("WHERE filters individual rows BEFORE grouping. HAVING filters groups AFTER GROUP BY. ORDER BY sorts results. GROUP BY aggregates rows. Example: SELECT * FROM users WHERE age > 18 AND status = 'active';")
                .tags("sql,filtering,where")
                .build(),

            Question.builder()
                .topic("SQL").difficulty("EASY").questionType("MCQ")
                .questionText("What does SELECT DISTINCT do in SQL?")
                .optionA("Selects NULL values").optionB("Selects the first row only").optionC("Removes duplicate rows from results").optionD("Counts unique values")
                .correctAnswer("C")
                .explanation("DISTINCT eliminates duplicate rows from the result set. Example: SELECT DISTINCT city FROM customers returns each city only once, even if multiple customers are from the same city. It operates on the combination of all selected columns.")
                .tags("sql,distinct,deduplication")
                .build(),

            Question.builder()
                .topic("SQL").difficulty("EASY").questionType("MCQ")
                .questionText("Which JOIN returns all rows from the left table and matched rows from the right table?")
                .optionA("INNER JOIN").optionB("RIGHT JOIN").optionC("FULL OUTER JOIN").optionD("LEFT JOIN")
                .correctAnswer("D")
                .explanation("LEFT JOIN (or LEFT OUTER JOIN) returns ALL rows from the left table. For rows where no match exists in the right table, NULL values are returned for right table columns. Useful for finding unmatched records, e.g., customers with no orders.")
                .tags("sql,joins,left-join")
                .build(),

            // ── SQL — MEDIUM ──────────────────────────────────────────
            Question.builder()
                .topic("SQL").difficulty("MEDIUM").questionType("MCQ")
                .questionText("What is the difference between DELETE and TRUNCATE in SQL?")
                .optionA("No difference").optionB("TRUNCATE can have a WHERE clause, DELETE cannot").optionC("DELETE logs individual rows and can be rolled back; TRUNCATE cannot be rolled back").optionD("TRUNCATE deletes the table structure")
                .correctAnswer("C")
                .explanation("DELETE is a DML statement: it logs each deleted row, fires triggers, can have WHERE clause, and can be rolled back within a transaction. TRUNCATE is DDL: it deallocates data pages, is much faster, cannot have WHERE, typically cannot be rolled back, and resets auto-increment counters.")
                .tags("sql,delete,truncate,transactions")
                .build(),

            Question.builder()
                .topic("SQL").difficulty("MEDIUM").questionType("MCQ")
                .questionText("What does the HAVING clause do in SQL?")
                .optionA("Filters rows before grouping").optionB("Sorts the result set").optionC("Filters groups after GROUP BY aggregation").optionD("Joins two tables")
                .correctAnswer("C")
                .explanation("HAVING filters the result of GROUP BY. WHERE cannot use aggregate functions; HAVING can. Example: SELECT department, AVG(salary) FROM employees GROUP BY department HAVING AVG(salary) > 50000 — returns only departments with average salary above 50k.")
                .tags("sql,having,groupby,aggregation")
                .build(),

            // ── REACT — EASY ──────────────────────────────────────────
            Question.builder()
                .topic("REACT").difficulty("EASY").questionType("MCQ")
                .questionText("What hook is used to add state to a functional component in React?")
                .optionA("useEffect").optionB("useContext").optionC("useRef").optionD("useState")
                .correctAnswer("D")
                .explanation("useState returns a pair: [currentValue, setterFunction]. When the setter is called with a new value, React re-renders the component. Example: const [count, setCount] = useState(0). The initial value (0) is only used on the first render.")
                .tags("react,hooks,useState")
                .build(),

            Question.builder()
                .topic("REACT").difficulty("EASY").questionType("MCQ")
                .questionText("What does the useEffect hook do in React?")
                .optionA("Manages component state").optionB("Creates a new component").optionC("Performs side effects after render").optionD("Memoizes a value")
                .correctAnswer("C")
                .explanation("useEffect runs after the component renders. It handles side effects: data fetching, subscriptions, DOM manipulation, timers. The dependency array controls when it re-runs: [] means once after mount, [value] means whenever value changes, no array means after every render.")
                .tags("react,hooks,useEffect,side-effects")
                .build(),

            Question.builder()
                .topic("REACT").difficulty("EASY").questionType("MCQ")
                .questionText("In React, what is 'props'?")
                .optionA("Internal component state").optionB("Data passed from parent to child component").optionC("A React hook").optionD("The component's lifecycle method")
                .correctAnswer("B")
                .explanation("Props (properties) are read-only data passed from a parent component to a child. They flow downward (one-way data flow). A child cannot modify its own props. For communication up (child to parent), pass a callback function as a prop.")
                .tags("react,props,components")
                .build(),

            // ── REACT — MEDIUM ────────────────────────────────────────
            Question.builder()
                .topic("REACT").difficulty("MEDIUM").questionType("MCQ")
                .questionText("What is the purpose of the React Context API?")
                .optionA("To fetch data from APIs").optionB("To avoid prop drilling by sharing state across the component tree").optionC("To manage component lifecycle").optionD("To optimize rendering performance")
                .correctAnswer("B")
                .explanation("Context provides a way to share values (theme, auth state, locale) across the component tree without passing props at every level (prop drilling). Context is not a replacement for all state management — use it for truly global state. For complex state, consider Redux or Zustand.")
                .tags("react,context,state-management")
                .build(),

            Question.builder()
                .topic("REACT").difficulty("MEDIUM").questionType("MCQ")
                .questionText("What does React.memo() do?")
                .optionA("Creates a memoized value").optionB("Prevents a component from re-rendering if its props haven't changed").optionC("Memoizes an expensive calculation").optionD("Creates a ref object")
                .correctAnswer("B")
                .explanation("React.memo() is a Higher Order Component that wraps a functional component and performs a shallow comparison of props. If props are identical to the previous render, React skips re-rendering and reuses the last result. Use for pure components that render the same output for same props.")
                .tags("react,performance,memo,optimization")
                .build(),

            // ── HR — EASY ─────────────────────────────────────────────
            Question.builder()
                .topic("HR").difficulty("EASY").questionType("TEXT")
                .questionText("Tell me about yourself. Provide a brief professional summary.")
                .correctAnswer(null) // TEXT questions have no single correct answer
                .explanation("A strong answer covers: your current role/education, 2-3 relevant skills or experiences, and why you're interested in this position. Keep it under 2 minutes. Focus on professional highlights, not personal history. End by connecting your background to the role.")
                .tags("hr,introduction,communication")
                .build(),

            Question.builder()
                .topic("HR").difficulty("EASY").questionType("TEXT")
                .questionText("What is your greatest professional strength and how have you applied it?")
                .correctAnswer(null)
                .explanation("Choose a strength relevant to the role. Structure: name the strength, give a specific example of using it, describe the measurable result. Avoid vague answers like 'I'm a hard worker'. Be specific: 'Problem-solving — I debugged a critical production issue that was causing 500 errors and reduced them by 95% in 2 hours.'")
                .tags("hr,strengths,behavioral")
                .build(),

            Question.builder()
                .topic("HR").difficulty("MEDIUM").questionType("TEXT")
                .questionText("Describe a situation where you had to work with a difficult team member. How did you handle it?")
                .correctAnswer(null)
                .explanation("Use the STAR method: Situation (context), Task (your responsibility), Action (what YOU specifically did — focus on your behavior, not blaming others), Result (positive outcome). Show emotional intelligence: communication, empathy, professionalism. Avoid speaking negatively about others.")
                .tags("hr,behavioral,star-method,teamwork")
                .build(),

            Question.builder()
                .topic("HR").difficulty("MEDIUM").questionType("TEXT")
                .questionText("Where do you see yourself in 5 years?")
                .correctAnswer(null)
                .explanation("Show ambition aligned with the company's growth path. Good answer: specific technical/leadership goals + how this role helps you get there + commitment to growing with the company. Avoid: 'I want your job', vague answers, or showing you see this as a stepping stone to leave soon.")
                .tags("hr,career-goals,motivation")
                .build(),

            Question.builder()
                .topic("HR").difficulty("HARD").questionType("TEXT")
                .questionText("Tell me about a time you failed. What did you learn from it?")
                .correctAnswer(null)
                .explanation("Interviewers want to see self-awareness, accountability, and growth mindset. Choose a real failure (not a humble-brag like 'I work too hard'). Structure: what happened, what your role was, what you learned specifically, and how you've applied that lesson since. Shows maturity and resilience.")
                .tags("hr,behavioral,failure,growth-mindset")
                .build()
        );
    }
}
