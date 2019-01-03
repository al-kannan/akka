# AKKA Features
## Summary
I have created few small programs to demonstrate AKKA concepts and features

### Hello World
This example shows 
- Akka System and Actor creation happens in the background
- Message are sent and control comes back to main code
- You can terminate Actor by sending PoisonPill
- After Actor is terminated, further messages will go to deadletter queue
- You can create multiple system within the same program
- Once we shutdown the systems then code comes to an end, otherwise it hangs for further instructions


### Ref Path Selection 
This example shows 
- Actor can be referenced by actorref
- Actor can be referenced by absolute path
- Actor can be referenced by relative path, but only inside of an another actor
- Actor can pring its absolute path

### State
This example shows 
- We can set our state info at the instanciation
- State can changed only within the actor and each actor maintains separately
- Actor cannot be restarted unless supervission is implemented 

### Actor Life Cycle
This example shows 
- There 4 methods that we can override 
- preStart happens when the actor is started
- postStop happens when the actor is stopped
- preRestart happens before actor gets restarted
- postRestart happens after the actor gets restarted
- Note: restart re initializes the state, to save it we need to do it at preRestart point to persitant storage
- Note: When actor fails default supervission is to restart the actor


### Parent Child
This example shows 
- Parent created child, then child created the sub child
- We can send message from parent to sub child directly
- We can kill the child, but sub child is not killed
- We can still send message to sub child 
- Selection seem to be restricted to only one level, I may be wrong

### Supervision
This example shows 
- In this example there is a GaurdianActor which creates the system and starts the parent actor
- Then GaurdianActor instructs parent actor to start child actor
- Then GaurdianActor instructs parent actor to send a Throw message to child actor
- Then child actor Throws StopException, this causes parent to stop the child and if we try to reach child we could not, message gets sent to dead letter
- Basically we cannot control GaurdianActor supervision strategy
- We can control rest of the tree in the Actor hierarchy and at each level we can set supervision strategy
- We need to note that if we restart parent then all child gets started

### Become UnBecome
This example shows 
- Basically Actor can have different behaviour in terms of what message to honor and at what point 
- You can declare multiple 'receive' sections and switch between them
- Use case would be when the Actor gets started first time and gets a message to process for the first 
  time we could do database connections and process the message, all subsequent messages can be processed 
  without reestablishing the database connection
- In this example some of the messages gets ignored if the behaviour is changed 

### FSM
This example shows 
- This is similar become/unbecome
- No receive function declared, it is handled by a different method called Events
- I don't see much difference 

### Timers and Scheduler
This example shows 
- An actor can call itself (via generating messages periodically)
- Scheduler can also be used, but Timers is preferred
- PoisonPill will stop the cycle

### Persistence
This is little bit tricky, if you under the trick it is no big deal
- The key point to understand is AKKA is persisting commands
- It persisting not only upon failure but also on regular shutdown and restart!!
- On each start it looks at the journal and plays all the commands to get to the right state (if you have any external variables, this could be a problem in logic, if it is a pure function with no side effects then this is okay)
- By storing commands we control what comands to be stored
- Journal holds all the commands, if you take a snapshot then Journal is only played from the snapshot point forward

- I created a simple example where my state is an Int variable
- I send a command to store value of 1, this gets persisted in the journal and gets applied to the state variable
- I also send a snap message to take a snapshot of my simple Int varibale, this can be complex object with function and struct data type with name=value pair
- When the actor fails, upon restart it recovers  
- When the actor is shutdown due to system shut down and when it comes back it recovers




