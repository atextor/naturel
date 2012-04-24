#!/bin/bash
rm -rf ../naturel/analysis
rm -rf ../naturel/lexer
rm -rf ../naturel/node
rm -rf ../parser

java -jar sablecc-3.2/lib/sablecc.jar -d .. grammatik-naturel.txt || exit 0

cd ../naturel/node
echo -e "\nFixing ILRNode-Interface"
for i in Plus Minus Div Mult Mod And Or Xor Eq Neq Lteq Lt Gteq Gt Dot; do
	name="A${i}Exp"
	echo "Fixing $name.java..."
	sed -i -e "s/$name extends PExp/$name extends PExp implements ILRNode/g" $name.java
	sed -i -e "s/import naturel.analysis.\*;/import naturel.analysis.\*;\nimport naturel.generator.ILRNode;/g" $name.java
done

echo -e "\nFixing IBoolNode-Interface"
for i in And Or Xor Eq Neq Lteq Lt Gteq Gt; do
	name="A${i}Exp"
	echo "Fixing $name.java..."
	sed -i -e "s/$name extends PExp implements ILRNode/$name extends PExp implements ILRNode, IBoolNode/g" $name.java
	sed -i -e "s/import naturel.analysis.\*;/import naturel.analysis.\*;\nimport naturel.generator.IBoolNode;/g" $name.java
done
echo "Fixing Node.java..."
sed -i -e "s/void parent/public void parent/g" Node.java
