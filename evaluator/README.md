# Automatic evaluation of tasks in the subject Database systems

The main topic of this project is the automation of task evaluation in the Database Systems course. The task involves translating a relational schema into an SQL schema and creating corresponding tables in a database system.

The evaluation tool was implemented in Java using the H2 database. The execution of the program is divided into four phases: initialization, parsing, checking and calculating points, and generating the result. It has been tested on 81 student solutions and successfully identifies the fulfillment of the task criteria in over 75% of the cases.

Once integrated into the [BRUTE](https://cw.felk.cvut.cz/brute/) system, the evaluation tool will function as follows: the student will upload a solution, and the tool will perform a thorough check and suggest a score based on a predefined distribution. If the student accepts the score and does not modify the solution, the teacher will manually review the remaining parts and assign the final score to the student.

## Run the evaluation tool

You can run the evaluation tool with the command:

```bash
java -jar evaluator.jar \
-sql solution/fileWithQueries.pdf \
-rm solution/fileWithRelationalModel.pdf \
-c fileWithCriteria.json \
-o outputFileName.htm
```
**Types of the options:**
- -c   contains path to the file with the evaluation criteria,
- -rm  contains path to the file with the relational model (optional),
- -sql contains path to the file with the SQL queries,
- -o   contains name of the output HTML file (optional).
