Taken from: http://answers.google.com/answers/threadview/id/452083.html

A formula of the propositional calculus is said to be in disjunctive
normal form (DNF) if it is a disjunction of elementary conjunctions. 
An elementary conjunction is a conjunction of some combination of
propositional variables and negations of propositional variables.

 1. Distribute negation (NOT) over any conjunctions or disjunctions
according to deMorgan's laws:

  ~(p & q) = ~p OR ~q

  ~(p OR q) = ~p & ~q

until negation is only applied to atomic propositions (propositional
variables). Remove double negations where these arise.  A
propositional variable or its negation will be called an elementary
proposition below.

 2. The algorithm then proceeds recursively on the results of the
first step.  The DNF of "P OR Q" is simply:

  (DNF(P)) OR (DNF(Q))

The DNF of "P & Q" is more complicated.  Start by finding the DNF of P, say:

   P_1 OR P_2 OR ... OR P_m

and the DNF of Q, say:

   Q_1 OR Q_2 OR ... OR Q_n

where each P_i, Q_j is a conjunction of elementary propositions.  Then
the DNF of "P & Q" is obtained as the disjunction of all possible
pairs of distributed conjunctions:

   ... OR ( P_i & Q_j ) OR ...

where I mean that the various elementary propositions of P_i and Q_j
are now to be conjuncted to form a single disjunction of the DNF for
"P & Q".

3. Optionally you may remove "inner" conjunctions which happen to
contain both an atomic proposition and its negation as these are
"false".  You may also remove duplicates of elementary propositions in
an "inner" conjunction.  Finally if the exact same "inner" conjunction
appears more than once in the "outer" disjunction, that duplication
can be eliminated by removing one copy.

Further pruning of the DNF may be possible but is not necessary in
order to satisfy the definition.  Such pruning is useful if one wishes
to obtain a "canonical" form of a formula, ie. to recognize when two
formulas are equivalent (in particular when a formula is tautologous),
but I'm not sure if this is important for your application.

Notes:  

  The disjunctive normal form is "dual" to the conjunctive normal form
(CNF), where the outer operation is a conjunction of elementary
disjunctions.  Any WFF of the propositional calculus can be expressed
in DNF or CNF.

  An empty conjunction is considered "true", by analogy with an empty
product being 1.  An empty disjunction is considered "false", by
analogy with an empty sum being 0.  This is consistent with the
proposed pruning/simplification rules above, as removing an obvious
"false" term from a disjunction should be interpreted as finally
"false" if all terms are removed.
