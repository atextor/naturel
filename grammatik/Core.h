#ifndef _CORE_H
#define _CORE_H
#include<stdio.h>
#include<stdlib.h>
#ifdef HANDLE_SIGSEGV
#include<signal.h>
#include<sys/types.h>
#include<execinfo.h>
#endif
#include<unistd.h>
#include<string.h>
#include<stdbool.h>
#include<unistd.h>

#ifdef __USE_ISOC99
typedef long long int num_t;
#define NUM_T_FORMAT "%lld"
#else
typedef long int num_t;
#define NUM_T_FORMAT "%ld"
#endif

typedef double fnum;
typedef struct SObject* Object;
typedef struct SNum* num;
typedef struct SString* str;
typedef struct SList* list;

struct SObject {
	str (*tostr)(void* this);
};

struct SString {
	struct SObject super;
	char* chars;
	num sze;
	num (*size)(str this);
	str (*append)(str this, str that);
	num (*asnum)(str this);
};

struct SNum {
	struct SObject super;
	num_t val;
	num (*addNum)(num this, num that);
	num (*subNum)(num this, num that);
	num (*multNum)(num this, num that);
	num (*divNum)(num this, num that);
	num (*modNum)(num this, num that);
	bool (*eq)(num this, num that);
	bool (*neq)(num this, num that);
	bool (*lteq)(num this, num that);
	bool (*lt)(num this, num that);
	bool (*gteq)(num this, num that);
	bool (*gt)(num this, num that);
};

struct SList {
	struct SObject super;
	Object x;
	list xs;
	void (*append)(list this, Object that);
};

/* Objekt-Prototypen */
Object createObject();
void initObject(Object this);
str Object_tostr(void* this);
Object Object_new();
num createNum();
void initNum(num this, num_t val);
str num_tostr(void* this);
num num_new(num_t val);
num num_addNum(num this, num that);
num num_subNum(num this, num that);
num num_multNum(num this, num that);
num num_divNum(num this, num that);
num num_modNum(num this, num that);
bool num_eq(num this, num that);
bool num_neq(num this, num that);
bool num_lteq(num this, num that);
bool num_lt(num this, num that);
bool num_gteq(num this, num that);
bool num_gt(num this, num that);
str createString();
void initString(str this, char* chars);
str str_tostr(void* this);
str str_new(char* chars);
num str_size(str this);
str str_append(str this, str that);
num str_asnum(str this);
list createList();
void initList(list this, Object x, list xs);
str list_tostr(void* this);
list list_new(Object x, list xs);

/* Library-Funktionen */
void out(str string);
str in();
void* getmem(int bytes);
#ifdef HANDLE_SIGSEGV
void sighandler(int signum);
#endif

#endif
