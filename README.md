# About Repository
본 저장소는 Java를 사용한 B+ Tree 구현 소스코드를 담고 있습니다.

<br/>

## 사용 언어

JAVA

<br/>

## 컴파일 방법

``` Shell
javac Main.java Ascending.java Data.java Node.java Operation.java Pair.java
```

<br/>

## 실행 방법
``` Shell
java -Xmx4096x Main <Command Line Arguments(실행 옵션)>
```
-Xmx 옵션은 필수적인 것은 아니지만, Heap의 최대 사이즈를 지정해 프로그램의 실행 속도를 향상시키기 위한 것으로 실행 환경을 고려하여 적절한 값으로 설정한다.

<br/>

## 실행 옵션

- 데이터 저장 파일 생성
  
  ``` Shell
  java Main -c <name of new data file> <max number of children>
  ```

- 데이터 삽입
  
  ``` Shell
  java Main -i <name of data file> <name of data file>
  ```

  data file은 csv 파일로서, 한 개 이상의 key-value 쌍을 다음과 같은 형식으로 저장한다.

  ```
  <key>,<value>\n
  ```
  
- 데이터 삭제
  
  ``` Shell
  java Main -d <name of data file> <name of data file>
  ```

  데이터 삭제에 사용되는 data file은 csv 파일로서, 한 개 이상의 key를 다음과 같은 형식으로 저장한다.

  ``` 
  <key>\n
  ```

- 단일 키 검색

  ``` Shell
  java Main -s <name of data file> <key to be searched>
  ``` 

  결과 탐색 경로에 존재하는 논리프 노드의 모든 키를 각 줄에 출력하고, 찾는 키의 값을 출력한다. 존재하지 않는다면 NOT FOUND를 출력한다.

- 구간 검색

  ``` Shell
  java Main -r <name of data file> <start key to be searched> <end key to be searched>
  ``` 
  
  start key와 end key 사이에 존재하는 모든 key-value 쌍을 출력한다.

<br/>

## 각 클래스의 멤버 변수, 메소드 및 알고리즘 설명

- Main
    
    Command Line Argument로 입력 받은 값으로 요구하는 B+ Tree Operation을 수행하 는 클래스이다.
    분기문을 통해 제어되며, 요구하는 Operation을 수행하는 메소드에 인자를 적절하게 전달한다.

- Node
    
    B+ Tree를 구성하는 노드 클래스이다.

  - isLeaf (boolean)
  
    Leaf Node라면 true, Non-Leaf Node라면 false의 값을 갖는다.

  - m (int)

    해당 Node가 갖고있는 key의 개수를 담는다.

  - r (Node)
    
    Leaf Node일 경우, 다음 Sibling 노드를 가리키고, Non-Leaf Node라면, Rightmost Sibling을 가리킨다.

  - p (ArrayList<Pair<Integer, Object>>)
    
    Key와 Left Child Node 또는 value를 담는 Arraylist이다. Leaf Node와 Non-Leaf Node를 동일한 Node 클래스로 구현하기 위해 Pair 클래스의 두 번째 Type Parameter를 모든 클래스의 최상위 클래스인 Object 클래스로 설정하였다.

- Pair<L, R>
  
    두 자료형의 값을 가질 수 있는 Generic Class이다. 해당 과제에서는 key와 value(또는 Left Child Node)를 담기 위해 사용되었다.

- Ascending
 
    Pair<Integer, Object> 클래스 객체들 간의 순서를 정의하기 위한 클래스이다. Pair 객체의 Integer 변수 값을 기준으로 오름차순으로 정렬된다.

- Data
    
    B+ Tree의 Root Node, Linked List 구성을 위한 ArrayList와 최대 Child Node의 개수를 필드로 갖는다.

- Operation
  
    B+ Tree Operation 수행을 담당하는 클래스이다.

  - For Create Operation

    - void create(String index_file, String m)
        
        인자로 받은 index_file을 만들어 Data 클래스 객체에 최대 Child Node의 개수 m을 담아 파일에 출력한다.

  - For Insert Operation

    - void insert(String index_file, String data_file)

        data_file에 담긴 key와 value를 B+ Tree에 삽입하도록 insertion 메소드를 호 출하고, 수정된 B+ Tree를 파일에 출력하도록 메소드를 호출한다.

    - void insertion(Node node, int key, int value)

        실질적인 B+ Tree 삽입 연산을 수행하는 메소드이다. 메소드를 재귀적으로 호출하여 key와 value를 삽입하기에 적절한 노드를 찾아 삽입한다. 그 후 Parent Node로 거슬러 올라가며 삽입된 Child Node에 Overflow가 일어났는 지 확인하고, Overflow가 일어났다면 각각 Node Split을 진행한다.

        Node Split은 key 배열의 중간 값(floor(m / 2)번째 인덱스)을 Parent로 올리 고, 그 key를 기준으로 두 개의 Node로 쪼개는 것을 말한다. 이를 위해 새 Node로 만들고, 그 노드로 key, value(또는 child)를 옮기는 연산을 진행하였다.

        Node Split은 Split되는 Node가 Leaf Node인지 Non-Leaf Node인지에 따라 다르다. Leaf Node일 경우, Parent Node로 올라간 key가 Child에 존재해야 하 지만 Non-Leaf Node는 그렇지 않으므로 분기문으로 이를 분리하여 구현하였다.

    - Node rootSplit(Node root)
  
        재귀적으로 insert를 수행하면 Root Node의 Overflow는 확인할 수 없다. 따 라서 insertion 메소드가 아닌 insert 메소드에서 한 key가 삽입된 후 Root Node에 Overflow가 발생했을 경우 호출하여 Root Node를 Split하는 메소드 이다.

  - For Delete Operation

    - void delete(String index_file, String data_file)
        data_file에 담긴 key를 B+ Tree에서 삭제하도록 deletion 메소드를 호출하고, 수정된 B+ Tree를 파일에 출력한다.
    
    - void deletion(Node node, int key)

        실질적인 B+ Tree 삭제 연산을 수행하는 메소드이다. 메소드를 재귀적으로 호출하여 key를 삭제하기에 적절한 노드를 찾아 key를 삭제한다. 그 후 Parent Node로 거슬러 올라가며 key가 삭제된 Child Node에 Underflow가 일어났는지 확인하고, Underflow가 일어났다면 적절한 연산을 진행한다.
        
      1. 자식 노드가 Leaf Node일 경우
         1. Sibling Node에서 Borrow한다.
         2. Sibling Node와 Merge한다.
      2. 자식 노드가 Non-Leaf Node일 경우
         1. Sibling Node에서 Borrow한다.
         2. Sibling Node와 Merge한다.
   
    - int childUnderflowChecker(Node node)

        Child Node에 Underflow가 일어났는지 확인하는 메소드이다. 자식 노드를 순차적으로 탐색하며 Underflow가 일어났는지 확인한다. Underflow가 일어 났다면 해당 노드의 인덱스를 반환한다. (단, Rightmost Child에서 Underflow 가 일어난 경우 -2를 반환한다.) Underflow가 일어나지 않은 경우에는 -1을 반환한다. 이를 이용하여 deletion 메소드에서 Underflow를 확인하고 적절한 메소드를 호출한다.

    - boolean leafBorrow(Node node, int idx)
  
        node의 Child Leaf Node에서 Underflow가 생긴 경우 Sibling Node에서 Borrow를 시도한다. 주위 노드에서 빌려올 수 있는 경우, 한 개의 key를 빌 려오며 Parent Node의 key를 적절한 수로 교체한다. Leaf Borrow에 성공한 경우 true를 그렇지 않은 경우 false를 반환하여 Leaf Merge 연산으로 넘어 갈 수 있도록 한다.

        이 경우 Node 클래스가 Rightmost Child(r)와 다른 Child를 분리하여 저장하 므로 다음과 같은 과정을 거쳤다.

        1. r에서 Underflow가 일어난 것이 아닌 경우
           1. Underflow가 마지막 key보다 작은 Child(ArrayList의 마지막 Child) 에서 일어나지 않았고 r이 아닌 Right Sibling에서 key를 빌려올 수 있는가?
           2. Underflow가 마지막 key보다 작은 Child 에서 일어났고 r에서 key 를 빌려올 수 있는가?
           3. Left Sibling에서 key를 빌려올 수 있는가? 
        2. r에서 Underflow가 일어났을 경우
           1. Left Sibling에서 key를 빌려올 수 있는가?
   
        위의 상황에 한 가지라도 포함된다면 Borrow에 성공한 것이므로 true를 반 환하였고, 그렇지 않다면 false를 반환하였다.

    - void leafMerge(Node node, int idx)

        Sibling Node에서 Borrow를 실패한 경우(Borrow에서 false 반환) Leaf Merge 를 한다. 두 개의 노드를 한 개의 노드로 합친다. 이 과정에서 Parent Node 의 key가 한 개 삭제된다. Merge는 Borrow할 수 없는 상황에 이루어지므로, 양 옆 어느 하나와 합쳐도 하나의 Node를 구성할 수 있음을 의미한다. 따 라서 Rightmost Child가 아니라면 오른쪽 Child와 Merge하였고, r이라면 왼 쪽 Child와 Merge하였다.

    - boolean nonLeafBorrow(Node node, int idx)

        Non Leaf Node에서 Underflow가 생긴 경우 Sibling Node에서 Borrow를 시도한다. 주위 노드에서 빌려올 수 있는 경우, 한 개의 key를 빌려오며 과정 에서 Key Rotation이 발생한다. Key Rotation이란 Parent Node의 한 key가 Child Node로 내려오고 빌림을 당하는 Node의 한 key가 Parent Node로 올 라가는 현상을 말한다. 이 과정에서 Sibling Node의 Child가 Underflow가 일 어난 Node로 이동한다. Non-Leaf Merge에 성공한 경우 true를 그렇지 않은 경우 false를 반환하여 Non-Leaf Merge 연산으로 넘어갈 수 있도록 한다. Node 클래스 구성으로 인한 케이스 분류는 leafBorrow와 유사하다. 마찬가 지로 성공 시 true 실패 시 false를 반환한다.
    
    - void nonLeafMerge(Node node, int idx)

        Sibling Node에서 Borrow를 실패한 경우 Leaf Merge를 한다. 두 개의 노드 를 한 개의 노드로 합친다. 이 과정에서 Parent Node의 key가 한 개 삭제되 고, 그 key가 Merge되는 노드에 추가된다. Merge는 Borrow할 수 없는 상황 에 이루어지므로, 양 옆 어느 하나와 합쳐도 하나의 Node를 구성할 수 있 음을 의미한다. 따라서 Rightmost Child가 아니라면 오른쪽 Child와 Merge 하였고, r이라면 왼쪽 Child와 Merge하였다.

    - void deleteKeyInNonLeaf(Node node, int key)
        
        B+ Tree에서 Leaf Node에서 key가 삭제된다면, 그 key는 Non-Leaf Node에 존재할 수 없다. 이를 막기 위해 삭제된 key가 node에 존재한다면, 해당 오 른쪽 Child Node의 가장 왼쪽 Leaf Node를 탐색해 그 Node에서 가장 작은 key값으로 그 key를 대체한다.

  - For Single Key Search

    - void singleSearch(String index_file, int key)
        
        B+ Tree에서 key 하나의 value를 찾는 메소드이다. Leaf Node를 만날 때까지 B+ Tree를 인덱스를 이용하여 탐색하며 거쳐가는 각 노드의 key들을 한 줄 에 출력하고, 마지막 줄에 찾는 key와 value를 출력한다. 해당 key를 찾지 못한 경우 “Not Found”를 출력한다.

  - For Ranged Search
  
    - void rangedSearch(String index_file, int start_key, int end_key)

        B+ Tree에서 start_key보다 크거나 같고 end_key보다 작거나 같은 범위 안에 있는 key와 value를 모두 출력하는 메소드이다. 반복문을 통해 start node와 end node를 찾고 범위 안에 있는 수를 순차적으로 출력한다. Leaf Node는 Linked List를 이루고 있으므로 start node만 찾고 순차적으로 탐색하며 출력할 수 있지만, 출력할 때마다 end_key와 비교하는 연산이 필요하므로 start node와 end node를 모두 찾는 방식을 사용하였다.

  - For Save and Load

    - void saveTree(String index_file, int m, Node root)

        Tree를 Serialize를 이용하여 저장한다. wirteObject() 메소드는 Node를 완벽 하게 저장하기 위하여 재귀적으로 Leaf Node까지 호출된다. 하지만 Leaf Node가 Linked List로 연결되어 있다면 Method call 이 스택에 쌓이게 되고 StackOverflow 예외가 발생하게 된다. 이를 방지하기 위하여 B+ Tree의 Linked List 연결을 모두 끊고, 그 내용을 ArrayList에 담아 파일에 출력한다.

    - Data buildTree(String index_file)

        파일에 저장된 객체를 불러와 ArrayList 배열로부터 B+ Tree의 Leaf Node Linked List를 복구하여 반환한다.

<br/>

## 테스트 결과

0부터 999999 까지의 수(100만 개)를 ArrayList에 삽입하고, 해당 리스트를 Collections 클래스의 shuffle 메소드를 이용하여 무작위로 섞어 파일에 출력하였고, 이를 삽입을 위 한 data_file로 사용하였다. (Delete를 위한 data_file도 동일)

실행 후 Ranged Search와 Single Key Search를 해본 결과 적절하게 수행되었고, 백 만 개 의 키 중 만 개의 수를 남겨놓고 삭제하는 연산 또한 원활하게 작동하였다.
