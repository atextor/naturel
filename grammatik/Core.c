#include "Core.h"

/* Number of bytes all objects use up */
long int objSize = 0;

/* Number of objects */
long int numObj = 0;

#ifdef HANDLE_SIGSEGV
/* Array for Stacktraces */
#define BUFSIZE 256
void* symbols[BUFSIZE];
#endif

/*
 * Base Object
 */
Object createObject() {
	Object obj = ((Object)getmem(sizeof(struct SObject)));
	return obj;
}

void initObject(Object this) {
	numObj++;
	this->tostr = Object_tostr;
}

Object Object_new() {
	Object this = createObject();
	initObject(this);
	return this;
}

str Object_tostr(void* this){
 	return str_new("Object()");
}

/*
 * String
 */
str createString() {
	str obj = ((str)getmem(sizeof(struct SString)));
	return obj;
}

void initString(str this, char* chars) {
	int i = 0;
	
	initObject((Object)this);
	((Object)this)->tostr = str_tostr;
	((str)this)->append = str_append;
	((str)this)->asnum = str_asnum;
	((str)this)->size = str_size;
	((str)this)->sze = num_new((num_t)strlen(chars));
	((str)this)->chars = (char*)getmem((this->sze->val + 1) * sizeof(char));
	((str)this)->chars[this->sze->val] = '\0';
	while (chars[i] != '\0') {
		this->chars[i] = chars[i];
		i++;
	}
}

num str_size(str this) {
	return this->sze;
}

str str_new(char* chars) {
	str this = createString();
	initString(this, chars);
	return this;
}

str str_tostr(void* this) {
	return (str)this;
}

str str_append(str this, str that) {
	int i;
	int j;
	int newsize = this->sze->val + that->sze->val;
	char* newchr = (char*)getmem(sizeof(char) * (newsize + 1));
	newchr[newsize] = '\0';
	i = 0;
	for (i = 0; i < this->sze->val; i++) {
		newchr[i] = this->chars[i];
	}
	j = i;
	for (i = 0; i < that->sze->val; i++) {
		newchr[i + j] = that->chars[i];
	} 

	return str_new(newchr);
}

num str_asnum(str this) {
	int i;
	i = atoi(this->chars);
	return num_new(i);
}

/*
 * Num
 */
num createNum() {
	num obj = ((num)getmem(sizeof(struct SNum)));
	return obj;
}

void initNum(num this, num_t val) {
	initObject((Object)this);
	((Object)this)->tostr = num_tostr;
	((num)this)->addNum = num_addNum;
	((num)this)->subNum = num_subNum;
	((num)this)->multNum = num_multNum;
	((num)this)->divNum = num_divNum;
	((num)this)->modNum = num_modNum;
	((num)this)->eq = num_eq;
	((num)this)->neq = num_neq;
	((num)this)->lteq = num_lteq;
	((num)this)->lt = num_lt;
	((num)this)->gteq = num_gteq;
	((num)this)->gt = num_gt;
	this->val = val;
}

str num_tostr(void* this) {
	/* TODO - Herausfinden, wie viele Stellen wir wirklich brauchen! */
	char* buf = (char*)getmem(16);
	num t = (num)this;
	
	
	/* snprintf ist besser, aber nicht in ANSI C verfügbar. */
#ifdef __USE_ISOC99
	snprintf(buf, 16, NUM_T_FORMAT, t->val);
#else
	sprintf(buf, NUM_T_FORMAT, t->val);
#endif
	return str_new(buf);
}

num num_new(num_t val) {
	num this = createNum();
	initNum(this, val);
	return this;
}

num num_addNum(num this, num that) {
	return num_new(this->val + that->val);	
}

num num_subNum(num this, num that) {
	return num_new(this->val - that->val);
}

num num_multNum(num this, num that) {
	return num_new(this->val * that->val);
}

num num_divNum(num this, num that) {
	return num_new(this->val / that->val);
}

num num_modNum(num this, num that) {
	return num_new(this->val % that->val);	
}

bool num_eq(num this, num that) {
	return (this->val == that->val);	
}

bool num_neq(num this, num that) {
	return (this->val != that->val);
}

bool num_lteq(num this, num that) {
	return (this->val <= that->val);	
}

bool num_lt(num this, num that) {
	return (this->val < that->val);
}

bool num_gteq(num this, num that) {
	return (this->val >= that->val);
}

bool num_gt(num this, num that) {
	return (this->val > that->val);
}

/*
 * List
 */
list createList() {
	list obj = ((list)getmem(sizeof(struct SList)));
	return obj;	
}

void initList(list this, Object x, list xs) {
	initObject((Object)this);
	((Object)this)->tostr = list_tostr;
	this->x = x;
	this->xs = xs;
}

str list_tostr(void* this) {
	str result;
	bool first;
	list t = (list)this;
	if (t == NULL || t->x == NULL) {
		return str_new("[]");
	}
	result = str_new("[");
	first = true;
	while (t->x != NULL) {
		if (first == true) {
			first = false;
		} else {
			result = str_append(result, str_new(","));
		}
		result = str_append(result, t->x->tostr(t->x));
		t = t->xs;
	}
	result = str_append(result, str_new("]"));
	return result;
}
	
list list_new(Object x, list xs) {
	list this = createList();
	initList(this, x, xs);
	return this;
}

/* Library-Funktionen */
void out(str string) {
	write(STDOUT_FILENO, string->chars, string->sze->val);
}

str in() {
	char input [256];
	input[255] = '\0';
	scanf("%255s", input);
	return str_new(input);
}

void* getmem(int bytes) {
	void* result = malloc(bytes);
	if (NULL == result) {
		printf("Out of cheese error\n");
		exit(EXIT_FAILURE);
	}
	memset(result, 0, bytes);
	objSize += bytes;
	return result;
}

#ifdef HANDLE_SIGSEGV
void sighandler(int signum) {
	int numPointers;
	switch (signum) {
		case SIGSEGV:
			fprintf(stderr, "\n*** Naturel: Caught SIGSEGV\n");
			fprintf(stderr, "Number of objects: %ld\n", numObj);
			fprintf(stderr, "Size of allocated memory: %ld Bytes\n", objSize);
			fprintf(stderr, "\nBacktrace:\n");
			numPointers = backtrace(symbols, BUFSIZE);
			backtrace_symbols_fd(symbols, numPointers, STDERR_FILENO);
			exit(EXIT_FAILURE);
			break;
		default:
			break;
	}
}
#endif

