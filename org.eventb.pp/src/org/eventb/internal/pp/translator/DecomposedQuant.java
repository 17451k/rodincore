/* Generated by TOM (version 2.2): Do not edit this file */
/*******************************************************************************
 * Copyright (c) 2006 ETH Zurich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eventb.internal.pp.translator;

import java.util.*;
import java.math.BigInteger;

import org.eventb.core.ast.*;


/**
 * ...
 * 
 * @author Matthias Konrad
 */
@SuppressWarnings("unused")
public class DecomposedQuant {

/* Generated by TOM (version 2.2): Do not edit this file */
/*******************************************************************************   * Copyright (c) 2006 ETH Zurich.   * All rights reserved. This program and the accompanying materials   * are made available under the terms of the Eclipse Public License v1.0   * which accompanies this distribution, and is available at   * http://www.eclipse.org/legal/epl-v10.html   *******************************************************************************/    /*   * Declaration of sorts used.   */    static private boolean tom_terms_equal_Predicate(Object t1, Object t2) { return  t1.equals(t2) ; }static private boolean tom_terms_equal_Expression(Object t1, Object t2) { return  t1.equals(t2) ; }static private boolean tom_terms_equal_Type(Object t1, Object t2) { return  t1.equals(t2) ; }static private boolean tom_terms_equal_BoundIdentDecl(Object t1, Object t2) { return  t1.equals(t2) ; }static private boolean tom_terms_equal_BigInteger(Object t1, Object t2) { return  t1.equals(t2) ; }static private boolean tom_terms_equal_PredicateList(Object t1, Object t2) { return  t1.equals(t2) ; }static private boolean tom_terms_equal_ExpressionList(Object t1, Object t2) { return  t1.equals(t2) ; }static private boolean tom_terms_equal_BoundIdentDeclList(Object t1, Object t2) { return  t1.equals(t2) ; }static private boolean tom_is_fun_sym_pList( Predicate[]  t) { return  true ; }static private int tom_get_size_pList_PredicateList( Predicate[]  t) { return  t.length ; }static private  Predicate  tom_get_element_pList_PredicateList( Predicate[]  t, int n) { return  t[n] ; }static private  Predicate[]  tom_empty_array_pList(int t) { return  null ; }static private  Predicate[]  tom_cons_array_pList( Predicate  e,  Predicate[]  t) { return  null ; }static private  Predicate[]  tom_get_slice_pList( Predicate[]  subject, int begin, int end) {
    Predicate[]  result = ( Predicate[] )tom_empty_array_pList(end - begin);
    while( begin != end ) {
      result = ( Predicate[] )tom_cons_array_pList(( Predicate )tom_get_element_pList_PredicateList(subject, begin),result);
      begin++;
     }
    return result;
  }

static private  Predicate[]  tom_append_array_pList( Predicate[]  l2,  Predicate[]  l1) {
    int size1 = tom_get_size_pList_PredicateList(l1);
    int size2 = tom_get_size_pList_PredicateList(l2);
    int index;
    Predicate[]  result = ( Predicate[] )tom_empty_array_pList(size1+size2);
    index=size1;
    while(index > 0) {
      result = ( Predicate[] )tom_cons_array_pList(( Predicate )tom_get_element_pList_PredicateList(l1,(size1-index)),result);
      index--;
    }
    index=size2;
    while(index > 0) {
      result = ( Predicate[] )tom_cons_array_pList(( Predicate )tom_get_element_pList_PredicateList(l2,(size2-index)),result);
      index--;
    }
    return result;
  }
static private boolean tom_is_fun_sym_eList( Expression[]  t) { return  true ; }static private int tom_get_size_eList_ExpressionList( Expression[]  t) { return  t.length ; }static private  Expression  tom_get_element_eList_ExpressionList( Expression[]  t, int n) { return  t[n] ; }static private  Expression[]  tom_empty_array_eList(int t) { return  null ; }static private  Expression[]  tom_cons_array_eList( Expression  e,  Expression[]  t) { return  null ; }static private  Expression[]  tom_get_slice_eList( Expression[]  subject, int begin, int end) {
    Expression[]  result = ( Expression[] )tom_empty_array_eList(end - begin);
    while( begin != end ) {
      result = ( Expression[] )tom_cons_array_eList(( Expression )tom_get_element_eList_ExpressionList(subject, begin),result);
      begin++;
     }
    return result;
  }

static private  Expression[]  tom_append_array_eList( Expression[]  l2,  Expression[]  l1) {
    int size1 = tom_get_size_eList_ExpressionList(l1);
    int size2 = tom_get_size_eList_ExpressionList(l2);
    int index;
    Expression[]  result = ( Expression[] )tom_empty_array_eList(size1+size2);
    index=size1;
    while(index > 0) {
      result = ( Expression[] )tom_cons_array_eList(( Expression )tom_get_element_eList_ExpressionList(l1,(size1-index)),result);
      index--;
    }
    index=size2;
    while(index > 0) {
      result = ( Expression[] )tom_cons_array_eList(( Expression )tom_get_element_eList_ExpressionList(l2,(size2-index)),result);
      index--;
    }
    return result;
  }
static private boolean tom_is_fun_sym_bidList( BoundIdentDecl[]  t) { return  true ; }static private int tom_get_size_bidList_BoundIdentDeclList( BoundIdentDecl[]  t) { return  t.length ; }static private  BoundIdentDecl  tom_get_element_bidList_BoundIdentDeclList( BoundIdentDecl[]  t, int n) { return  t[n] ; }static private  BoundIdentDecl[]  tom_empty_array_bidList(int t) { return  null ; }static private  BoundIdentDecl[]  tom_cons_array_bidList( BoundIdentDecl  e,  BoundIdentDecl[]  t) { return  null ; }static private  BoundIdentDecl[]  tom_get_slice_bidList( BoundIdentDecl[]  subject, int begin, int end) {
    BoundIdentDecl[]  result = ( BoundIdentDecl[] )tom_empty_array_bidList(end - begin);
    while( begin != end ) {
      result = ( BoundIdentDecl[] )tom_cons_array_bidList(( BoundIdentDecl )tom_get_element_bidList_BoundIdentDeclList(subject, begin),result);
      begin++;
     }
    return result;
  }

static private  BoundIdentDecl[]  tom_append_array_bidList( BoundIdentDecl[]  l2,  BoundIdentDecl[]  l1) {
    int size1 = tom_get_size_bidList_BoundIdentDeclList(l1);
    int size2 = tom_get_size_bidList_BoundIdentDeclList(l2);
    int index;
    BoundIdentDecl[]  result = ( BoundIdentDecl[] )tom_empty_array_bidList(size1+size2);
    index=size1;
    while(index > 0) {
      result = ( BoundIdentDecl[] )tom_cons_array_bidList(( BoundIdentDecl )tom_get_element_bidList_BoundIdentDeclList(l1,(size1-index)),result);
      index--;
    }
    index=size2;
    while(index > 0) {
      result = ( BoundIdentDecl[] )tom_cons_array_bidList(( BoundIdentDecl )tom_get_element_bidList_BoundIdentDeclList(l2,(size2-index)),result);
      index--;
    }
    return result;
  }
    /*   * AssociativePredicate   */     static private boolean tom_is_fun_sym_AssociativePredicate( Predicate  t) { return  t instanceof AssociativePredicate ; }static private  Predicate[]  tom_get_slot_AssociativePredicate_children( Predicate  t) { return  ((AssociativePredicate) t).getChildren() ; }static private boolean tom_is_fun_sym_Land( Predicate  t) { return  t != null && t.getTag() == Formula.LAND ; }static private  Predicate[]  tom_get_slot_Land_children( Predicate  t) { return  ((AssociativePredicate) t).getChildren() ; }static private boolean tom_is_fun_sym_Lor( Predicate  t) { return  t != null && t.getTag() == Formula.LOR ; }static private  Predicate[]  tom_get_slot_Lor_children( Predicate  t) { return  ((AssociativePredicate) t).getChildren() ; }      /*   * BinaryPredicate   */     static private boolean tom_is_fun_sym_BinaryPredicate( Predicate  t) { return  t instanceof BinaryPredicate ; }static private  Predicate  tom_get_slot_BinaryPredicate_left( Predicate  t) { return  ((BinaryPredicate) t).getLeft() ; }static private  Predicate  tom_get_slot_BinaryPredicate_right( Predicate  t) { return  ((BinaryPredicate) t).getRight() ; }static private boolean tom_is_fun_sym_Limp( Predicate  t) { return  t != null && t.getTag() == Formula.LIMP ; }static private  Predicate  tom_get_slot_Limp_left( Predicate  t) { return  ((BinaryPredicate) t).getLeft() ; }static private  Predicate  tom_get_slot_Limp_right( Predicate  t) { return  ((BinaryPredicate) t).getRight() ; }static private boolean tom_is_fun_sym_Leqv( Predicate  t) { return  t != null && t.getTag() == Formula.LEQV ; }static private  Predicate  tom_get_slot_Leqv_left( Predicate  t) { return  ((BinaryPredicate) t). getLeft() ; }static private  Predicate  tom_get_slot_Leqv_right( Predicate  t) { return  ((BinaryPredicate) t).getRight() ; }    /*   * UnaryPredicate   */    static private boolean tom_is_fun_sym_UnaryPredicate( Predicate  t) { return  t instanceof UnaryPredicate ; }static private  Predicate  tom_get_slot_UnaryPredicate_child( Predicate  t) { return  ((UnaryPredicate) t). getChild() ; }static private boolean tom_is_fun_sym_Not( Predicate  t) { return  t != null && t.getTag() == Formula.NOT ; }static private  Predicate  tom_get_slot_Not_child( Predicate  t) { return  ((UnaryPredicate) t). getChild() ; }    /*   * RelationalPredicate   */    static private boolean tom_is_fun_sym_RelationalPredicate( Predicate  t) { return  t instanceof RelationalPredicate ; }static private  Expression  tom_get_slot_RelationalPredicate_left( Predicate  t) { return  ((RelationalPredicate) t). getLeft() ; }static private  Expression  tom_get_slot_RelationalPredicate_right( Predicate  t) { return  ((RelationalPredicate) t).getRight() ; }static private boolean tom_is_fun_sym_Equal( Predicate  t) { return  t != null && t.getTag() == Formula.EQUAL ; }static private  Expression  tom_get_slot_Equal_left( Predicate  t) { return  ((RelationalPredicate) t). getLeft() ; }static private  Expression  tom_get_slot_Equal_right( Predicate  t) { return  ((RelationalPredicate) t).getRight() ; }static private boolean tom_is_fun_sym_NotEqual( Predicate  t) { return  t != null && t.getTag() == Formula.NOTEQUAL ; }static private  Expression  tom_get_slot_NotEqual_left( Predicate  t) { return  ((RelationalPredicate) t). getLeft() ; }static private  Expression  tom_get_slot_NotEqual_right( Predicate  t) { return  ((RelationalPredicate) t).getRight() ; }static private boolean tom_is_fun_sym_Lt( Predicate  t) { return  t != null && t.getTag() == Formula.LT ; }static private  Expression  tom_get_slot_Lt_left( Predicate  t) { return  ((RelationalPredicate) t). getLeft() ; }static private  Expression  tom_get_slot_Lt_right( Predicate  t) { return  ((RelationalPredicate) t).getRight() ; }static private boolean tom_is_fun_sym_Le( Predicate  t) { return  t != null && t.getTag() == Formula.LE ; }static private  Expression  tom_get_slot_Le_left( Predicate  t) { return  ((RelationalPredicate) t). getLeft() ; }static private  Expression  tom_get_slot_Le_right( Predicate  t) { return  ((RelationalPredicate) t).getRight() ; }static private boolean tom_is_fun_sym_Gt( Predicate  t) { return  t != null && t.getTag() == Formula.GT ; }static private  Expression  tom_get_slot_Gt_left( Predicate  t) { return  ((RelationalPredicate) t). getLeft() ; }static private  Expression  tom_get_slot_Gt_right( Predicate  t) { return  ((RelationalPredicate) t).getRight() ; }static private boolean tom_is_fun_sym_Ge( Predicate  t) { return  t != null && t.getTag() == Formula.GE ; }static private  Expression  tom_get_slot_Ge_left( Predicate  t) { return  ((RelationalPredicate) t). getLeft() ; }static private  Expression  tom_get_slot_Ge_right( Predicate  t) { return  ((RelationalPredicate) t).getRight() ; }static private boolean tom_is_fun_sym_In( Predicate  t) { return  t != null && t.getTag() == Formula.IN ; }static private  Expression  tom_get_slot_In_left( Predicate  t) { return  ((RelationalPredicate) t). getLeft() ; }static private  Expression  tom_get_slot_In_right( Predicate  t) { return  ((RelationalPredicate) t).getRight() ; }static private boolean tom_is_fun_sym_NotIn( Predicate  t) { return  t != null && t.getTag() == Formula.NOTIN ; }static private  Expression  tom_get_slot_NotIn_left( Predicate  t) { return  ((RelationalPredicate) t). getLeft() ; }static private  Expression  tom_get_slot_NotIn_right( Predicate  t) { return  ((RelationalPredicate) t).getRight() ; }static private boolean tom_is_fun_sym_Subset( Predicate  t) { return  t != null && t.getTag() == Formula.SUBSET ; }static private  Expression  tom_get_slot_Subset_left( Predicate  t) { return  ((RelationalPredicate) t). getLeft() ; }static private  Expression  tom_get_slot_Subset_right( Predicate  t) { return  ((RelationalPredicate) t).getRight() ; }static private boolean tom_is_fun_sym_NotSubset( Predicate  t) { return  t != null && t.getTag() == Formula.NOTSUBSET ; }static private  Expression  tom_get_slot_NotSubset_left( Predicate  t) { return  ((RelationalPredicate) t). getLeft() ; }static private  Expression  tom_get_slot_NotSubset_right( Predicate  t) { return  ((RelationalPredicate) t).getRight() ; }static private boolean tom_is_fun_sym_SubsetEq( Predicate  t) { return  t != null && t.getTag() == Formula.SUBSETEQ ; }static private  Expression  tom_get_slot_SubsetEq_left( Predicate  t) { return  ((RelationalPredicate) t). getLeft() ; }static private  Expression  tom_get_slot_SubsetEq_right( Predicate  t) { return  ((RelationalPredicate) t).getRight() ; }static private boolean tom_is_fun_sym_NotSubsetEq( Predicate  t) { return  t != null && t.getTag() == Formula.NOTSUBSETEQ ; }static private  Expression  tom_get_slot_NotSubsetEq_left( Predicate  t) { return  ((RelationalPredicate) t). getLeft() ; }static private  Expression  tom_get_slot_NotSubsetEq_right( Predicate  t) { return  ((RelationalPredicate) t).getRight() ; }    /*   * QuantifiedPredicate   */    static private boolean tom_is_fun_sym_QuantifiedPredicate( Predicate  t) { return  t instanceof QuantifiedPredicate ; }static private  BoundIdentDecl[]  tom_get_slot_QuantifiedPredicate_identifiers( Predicate  t) { return  ((QuantifiedPredicate)t).getBoundIdentDecls() ; }static private  Predicate  tom_get_slot_QuantifiedPredicate_predicate( Predicate  t) { return  ((QuantifiedPredicate)t).getPredicate() ; }static private boolean tom_is_fun_sym_ForAll( Predicate  t) { return  t != null && t.getTag() == Formula.FORALL ; }static private  BoundIdentDecl[]  tom_get_slot_ForAll_identifiers( Predicate  t) { return  ((QuantifiedPredicate)t).getBoundIdentDecls() ; }static private  Predicate  tom_get_slot_ForAll_predicate( Predicate  t) { return  ((QuantifiedPredicate)t).getPredicate() ; }static private boolean tom_is_fun_sym_Exists( Predicate  t) { return  t != null && t.getTag() == Formula.EXISTS ; }static private  BoundIdentDecl[]  tom_get_slot_Exists_identifiers( Predicate  t) { return  ((QuantifiedPredicate)t).getBoundIdentDecls() ; }static private  Predicate  tom_get_slot_Exists_predicate( Predicate  t) { return  ((QuantifiedPredicate)t).getPredicate() ; }    /*    * LiteralPredicate   */    static private boolean tom_is_fun_sym_LiteralPredicate( Predicate  t) { return  t instanceof LiteralPredicate ; }static private boolean tom_is_fun_sym_BTRUE( Predicate  t) { return  t != null && t.getTag() == Formula.BTRUE ; }static private boolean tom_is_fun_sym_BFALSE( Predicate  t) { return  t != null && t.getTag() == Formula.BFALSE ; }    /*   * SimplePredicate   */     static private boolean tom_is_fun_sym_SimplePredicate( Predicate  t) { return  t instanceof SimplePredicate; }static private  Expression  tom_get_slot_SimplePredicate_child( Predicate  t) { return  ((SimplePredicate) t).getExpression() ; }static private boolean tom_is_fun_sym_Finite( Predicate  t) { return  t != null && t.getTag() == Formula.KFINITE ; }static private  Expression  tom_get_slot_Finite_child( Predicate  t) { return  ((SimplePredicate) t).getExpression() ; }    /*   * AssociativeExpression   */    static private boolean tom_is_fun_sym_AssociativeExpression( Expression  t) { return  t instanceof AssociativeExpression ; }static private  Expression[]  tom_get_slot_AssociativeExpression_children( Expression  t) { return  ((AssociativeExpression) t).getChildren() ; }static private boolean tom_is_fun_sym_BUnion( Expression  t) { return  t != null && t.getTag() == Formula.BUNION ; }static private  Expression[]  tom_get_slot_BUnion_children( Expression  t) { return  ((AssociativeExpression) t).getChildren() ; }static private boolean tom_is_fun_sym_BInter( Expression  t) { return  t != null && t.getTag() == Formula.BINTER ; }static private  Expression[]  tom_get_slot_BInter_children( Expression  t) { return  ((AssociativeExpression) t).getChildren() ; }static private boolean tom_is_fun_sym_Bcomp( Expression  t) { return  t != null && t.getTag() == Formula.BCOMP ; }static private  Expression[]  tom_get_slot_Bcomp_children( Expression  t) { return  ((AssociativeExpression) t).getChildren() ; }static private boolean tom_is_fun_sym_Fcomp( Expression  t) { return  t != null && t.getTag() == Formula.FCOMP ; }static private  Expression[]  tom_get_slot_Fcomp_children( Expression  t) { return  ((AssociativeExpression) t).getChildren() ; }static private boolean tom_is_fun_sym_Ovr( Expression  t) { return  t != null && t.getTag() == Formula.OVR ; }static private  Expression[]  tom_get_slot_Ovr_children( Expression  t) { return  ((AssociativeExpression) t).getChildren() ; }static private boolean tom_is_fun_sym_Plus( Expression  t) { return  t != null && t.getTag() == Formula.PLUS ; }static private  Expression[]  tom_get_slot_Plus_children( Expression  t) { return  ((AssociativeExpression) t).getChildren() ; }static private boolean tom_is_fun_sym_Mul( Expression  t) { return  t != null && t.getTag() == Formula.MUL ; }static private  Expression[]  tom_get_slot_Mul_children( Expression  t) { return  ((AssociativeExpression) t).getChildren() ; }     /*   * AtomicExpression   */     static private boolean tom_is_fun_sym_AtomicExpression( Expression  t) { return  t instanceof AtomicExpression ; }static private boolean tom_is_fun_sym_Natural( Expression  t) { return  t != null && t.getTag() == Formula.NATURAL ; }static private boolean tom_is_fun_sym_Natural1( Expression  t) { return  t != null && t.getTag() == Formula.NATURAL1 ; }static private boolean tom_is_fun_sym_INTEGER( Expression  t) { return  t != null && t.getTag() == Formula.INTEGER ; }static private boolean tom_is_fun_sym_BOOL( Expression  t) { return  t != null && t.getTag() == Formula.BOOL ; }static private boolean tom_is_fun_sym_TRUE( Expression  t) { return  t != null && t.getTag() == Formula.TRUE ; }static private boolean tom_is_fun_sym_FALSE( Expression  t) { return  t != null && t.getTag() == Formula.FALSE ; }static private boolean tom_is_fun_sym_EmptySet( Expression  t) { return  t != null && t.getTag() == Formula.EMPTYSET ; }static private boolean tom_is_fun_sym_PRED( Expression  t) { return  t != null && t.getTag() == Formula.KPRED ; }static private boolean tom_is_fun_sym_SUCC( Expression  t) { return  t != null && t.getTag() == Formula.KSUCC ; }      /*   * BinaryExpression   */    static private boolean tom_is_fun_sym_BinaryExpression( Expression  t) { return  t instanceof BinaryExpression ; }static private  Expression  tom_get_slot_BinaryExpression_left( Expression  t) { return  ((BinaryExpression) t).getLeft() ; }static private  Expression  tom_get_slot_BinaryExpression_right( Expression  t) { return  ((BinaryExpression) t).getRight() ; }static private boolean tom_is_fun_sym_Mapsto( Expression  t) { return  t != null && t.getTag() == Formula.MAPSTO ; }static private  Expression  tom_get_slot_Mapsto_left( Expression  t) { return  ((BinaryExpression) t).getLeft() ; }static private  Expression  tom_get_slot_Mapsto_right( Expression  t) { return  ((BinaryExpression) t).getRight() ; }static private boolean tom_is_fun_sym_Rel( Expression  t) { return  t != null && t.getTag() == Formula.REL ; }static private  Expression  tom_get_slot_Rel_left( Expression  t) { return  ((BinaryExpression) t).getLeft() ; }static private  Expression  tom_get_slot_Rel_right( Expression  t) { return  ((BinaryExpression) t).getRight() ; }static private boolean tom_is_fun_sym_Trel( Expression  t) { return  t != null && t.getTag() == Formula.TREL ; }static private  Expression  tom_get_slot_Trel_left( Expression  t) { return  ((BinaryExpression) t).getLeft() ; }static private  Expression  tom_get_slot_Trel_right( Expression  t) { return  ((BinaryExpression) t).getRight() ; }static private boolean tom_is_fun_sym_Srel( Expression  t) { return  t != null && t.getTag() == Formula.SREL ; }static private  Expression  tom_get_slot_Srel_left( Expression  t) { return  ((BinaryExpression) t).getLeft() ; }static private  Expression  tom_get_slot_Srel_right( Expression  t) { return  ((BinaryExpression) t).getRight() ; }static private boolean tom_is_fun_sym_Strel( Expression  t) { return  t != null && t.getTag() == Formula.STREL ; }static private  Expression  tom_get_slot_Strel_left( Expression  t) { return  ((BinaryExpression) t).getLeft() ; }static private  Expression  tom_get_slot_Strel_right( Expression  t) { return  ((BinaryExpression) t).getRight() ; }static private boolean tom_is_fun_sym_Pfun( Expression  t) { return  t != null && t.getTag() == Formula.PFUN ; }static private  Expression  tom_get_slot_Pfun_left( Expression  t) { return  ((BinaryExpression) t).getLeft() ; }static private  Expression  tom_get_slot_Pfun_right( Expression  t) { return  ((BinaryExpression) t).getRight() ; }static private boolean tom_is_fun_sym_Tfun( Expression  t) { return  t != null && t.getTag() == Formula.TFUN ; }static private  Expression  tom_get_slot_Tfun_left( Expression  t) { return  ((BinaryExpression) t).getLeft() ; }static private  Expression  tom_get_slot_Tfun_right( Expression  t) { return  ((BinaryExpression) t).getRight() ; }static private boolean tom_is_fun_sym_Pinj( Expression  t) { return  t != null && t.getTag() == Formula.PINJ ; }static private  Expression  tom_get_slot_Pinj_left( Expression  t) { return  ((BinaryExpression) t).getLeft() ; }static private  Expression  tom_get_slot_Pinj_right( Expression  t) { return  ((BinaryExpression) t).getRight() ; }static private boolean tom_is_fun_sym_Tinj( Expression  t) { return  t != null && t.getTag() == Formula.TINJ ; }static private  Expression  tom_get_slot_Tinj_left( Expression  t) { return  ((BinaryExpression) t).getLeft() ; }static private  Expression  tom_get_slot_Tinj_right( Expression  t) { return  ((BinaryExpression) t).getRight() ; }static private boolean tom_is_fun_sym_Psur( Expression  t) { return  t != null && t.getTag() == Formula.PSUR ; }static private  Expression  tom_get_slot_Psur_left( Expression  t) { return  ((BinaryExpression) t).getLeft() ; }static private  Expression  tom_get_slot_Psur_right( Expression  t) { return  ((BinaryExpression) t).getRight() ; }static private boolean tom_is_fun_sym_Tsur( Expression  t) { return  t != null && t.getTag() == Formula.TSUR ; }static private  Expression  tom_get_slot_Tsur_left( Expression  t) { return  ((BinaryExpression) t).getLeft() ; }static private  Expression  tom_get_slot_Tsur_right( Expression  t) { return  ((BinaryExpression) t).getRight() ; }static private boolean tom_is_fun_sym_Tbij( Expression  t) { return  t != null && t.getTag() == Formula.TBIJ ; }static private  Expression  tom_get_slot_Tbij_left( Expression  t) { return  ((BinaryExpression) t).getLeft() ; }static private  Expression  tom_get_slot_Tbij_right( Expression  t) { return  ((BinaryExpression) t).getRight() ; }static private boolean tom_is_fun_sym_SetMinus( Expression  t) { return  t != null && t.getTag() == Formula.SETMINUS ; }static private  Expression  tom_get_slot_SetMinus_left( Expression  t) { return  ((BinaryExpression) t).getLeft() ; }static private  Expression  tom_get_slot_SetMinus_right( Expression  t) { return  ((BinaryExpression) t).getRight() ; }static private boolean tom_is_fun_sym_Cprod( Expression  t) { return  t != null && t.getTag() == Formula.CPROD ; }static private  Expression  tom_get_slot_Cprod_left( Expression  t) { return  ((BinaryExpression) t).getLeft() ; }static private  Expression  tom_get_slot_Cprod_right( Expression  t) { return  ((BinaryExpression) t).getRight() ; }static private boolean tom_is_fun_sym_Dprod( Expression  t) { return  t != null && t.getTag() == Formula.DPROD ; }static private  Expression  tom_get_slot_Dprod_left( Expression  t) { return  ((BinaryExpression) t).getLeft() ; }static private  Expression  tom_get_slot_Dprod_right( Expression  t) { return  ((BinaryExpression) t).getRight() ; }static private boolean tom_is_fun_sym_Pprod( Expression  t) { return  t != null && t.getTag() == Formula.PPROD ; }static private  Expression  tom_get_slot_Pprod_left( Expression  t) { return  ((BinaryExpression) t).getLeft() ; }static private  Expression  tom_get_slot_Pprod_right( Expression  t) { return  ((BinaryExpression) t).getRight() ; }static private boolean tom_is_fun_sym_DomRes( Expression  t) { return  t != null && t.getTag() == Formula.DOMRES ; }static private  Expression  tom_get_slot_DomRes_left( Expression  t) { return  ((BinaryExpression) t).getLeft() ; }static private  Expression  tom_get_slot_DomRes_right( Expression  t) { return  ((BinaryExpression) t).getRight() ; }static private boolean tom_is_fun_sym_DomSub( Expression  t) { return  t != null && t.getTag() == Formula.DOMSUB ; }static private  Expression  tom_get_slot_DomSub_left( Expression  t) { return  ((BinaryExpression) t).getLeft() ; }static private  Expression  tom_get_slot_DomSub_right( Expression  t) { return  ((BinaryExpression) t).getRight() ; }static private boolean tom_is_fun_sym_RanRes( Expression  t) { return  t != null && t.getTag() == Formula.RANRES ; }static private  Expression  tom_get_slot_RanRes_left( Expression  t) { return  ((BinaryExpression) t).getLeft() ; }static private  Expression  tom_get_slot_RanRes_right( Expression  t) { return  ((BinaryExpression) t).getRight() ; }static private boolean tom_is_fun_sym_RanSub( Expression  t) { return  t != null && t.getTag() == Formula.RANSUB ; }static private  Expression  tom_get_slot_RanSub_left( Expression  t) { return  ((BinaryExpression) t).getLeft() ; }static private  Expression  tom_get_slot_RanSub_right( Expression  t) { return  ((BinaryExpression) t).getRight() ; }static private boolean tom_is_fun_sym_UpTo( Expression  t) { return  t != null && t.getTag() == Formula.UPTO ; }static private  Expression  tom_get_slot_UpTo_left( Expression  t) { return  ((BinaryExpression) t).getLeft() ; }static private  Expression  tom_get_slot_UpTo_right( Expression  t) { return  ((BinaryExpression) t).getRight() ; }static private boolean tom_is_fun_sym_Minus( Expression  t) { return  t != null && t.getTag() == Formula.MINUS ; }static private  Expression  tom_get_slot_Minus_left( Expression  t) { return  ((BinaryExpression) t).getLeft() ; }static private  Expression  tom_get_slot_Minus_right( Expression  t) { return  ((BinaryExpression) t).getRight() ; }static private boolean tom_is_fun_sym_Div( Expression  t) { return  t != null && t.getTag() == Formula.DIV ; }static private  Expression  tom_get_slot_Div_left( Expression  t) { return  ((BinaryExpression) t).getLeft() ; }static private  Expression  tom_get_slot_Div_right( Expression  t) { return  ((BinaryExpression) t).getRight() ; }static private boolean tom_is_fun_sym_Mod( Expression  t) { return  t != null && t.getTag() == Formula.MOD ; }static private  Expression  tom_get_slot_Mod_left( Expression  t) { return  ((BinaryExpression) t).getLeft() ; }static private  Expression  tom_get_slot_Mod_right( Expression  t) { return  ((BinaryExpression) t).getRight() ; }static private boolean tom_is_fun_sym_Expn( Expression  t) { return  t != null && t.getTag() == Formula.EXPN ; }static private  Expression  tom_get_slot_Expn_left( Expression  t) { return  ((BinaryExpression) t).getLeft() ; }static private  Expression  tom_get_slot_Expn_right( Expression  t) { return  ((BinaryExpression) t).getRight() ; }static private boolean tom_is_fun_sym_FunImage( Expression  t) { return  t != null && t.getTag() == Formula.FUNIMAGE ; }static private  Expression  tom_get_slot_FunImage_left( Expression  t) { return  ((BinaryExpression) t).getLeft() ; }static private  Expression  tom_get_slot_FunImage_right( Expression  t) { return  ((BinaryExpression) t).getRight() ; }static private boolean tom_is_fun_sym_RelImage( Expression  t) { return  t != null && t.getTag() == Formula.RELIMAGE ; }static private  Expression  tom_get_slot_RelImage_left( Expression  t) { return  ((BinaryExpression) t).getLeft() ; }static private  Expression  tom_get_slot_RelImage_right( Expression  t) { return  ((BinaryExpression) t).getRight() ; }    /*   * BoolExpression   */    static private boolean tom_is_fun_sym_Bool( Expression  t) { return  t != null && t.getTag() == Formula.KBOOL ; }static private  Predicate  tom_get_slot_Bool_predicate( Expression  t) { return  ((BoolExpression) t).getPredicate() ; }    /*   * Identifier   */    static private boolean tom_is_fun_sym_Identifier( Expression  t) { return  t instanceof Identifier ; }    /*   * FreeIdentifier   */  static private boolean tom_is_fun_sym_FreeIdentifier( Expression  t) { return  t != null && t.getTag() == Formula.FREE_IDENT ; }static private String tom_get_slot_FreeIdentifier_name( Expression  t) { return  ((FreeIdentifier)t).getName() ; }    /*   * BoundIdentifier   */  static private boolean tom_is_fun_sym_BoundIdentifier( Expression  t) { return  t != null && t.getTag() == Formula.BOUND_IDENT ; }static private int tom_get_slot_BoundIdentifier_boundIndex( Expression  t) { return  ((BoundIdentifier)t).getBoundIndex() ; }    /*   * IntegerLiteral   */    static private boolean tom_is_fun_sym_IntegerLiteral( Expression  t) { return  t instanceof IntegerLiteral ; }static private  BigInteger  tom_get_slot_IntegerLiteral_value( Expression  t) { return  ((IntegerLiteral) t).getValue() ; }    /*   * QuantifiedExpression   */    static private boolean tom_is_fun_sym_Cset( Expression  t) { return  t != null && t.getTag() == Formula.CSET ; }static private  BoundIdentDecl[]  tom_get_slot_Cset_identifiers( Expression  t) { return  ((QuantifiedExpression)t).getBoundIdentDecls() ; }static private  Predicate  tom_get_slot_Cset_predicate( Expression  t) { return  ((QuantifiedExpression)t).getPredicate() ; }static private  Expression  tom_get_slot_Cset_expression( Expression  t) { return  ((QuantifiedExpression)t).getExpression() ; }static private boolean tom_is_fun_sym_Qinter( Expression  t) { return  t != null && t.getTag() == Formula.QINTER ; }static private  BoundIdentDecl[]  tom_get_slot_Qinter_identifiers( Expression  t) { return  ((QuantifiedExpression)t).getBoundIdentDecls() ; }static private  Predicate  tom_get_slot_Qinter_predicate( Expression  t) { return  ((QuantifiedExpression)t).getPredicate() ; }static private  Expression  tom_get_slot_Qinter_expression( Expression  t) { return  ((QuantifiedExpression)t).getExpression() ; }static private boolean tom_is_fun_sym_Qunion( Expression  t) { return  t != null && t.getTag() == Formula.QUNION ; }static private  BoundIdentDecl[]  tom_get_slot_Qunion_identifiers( Expression  t) { return  ((QuantifiedExpression)t).getBoundIdentDecls() ; }static private  Predicate  tom_get_slot_Qunion_predicate( Expression  t) { return  ((QuantifiedExpression)t).getPredicate() ; }static private  Expression  tom_get_slot_Qunion_expression( Expression  t) { return  ((QuantifiedExpression)t).getExpression() ; }    /*   * SetExtension   */    static private boolean tom_is_fun_sym_SetExtension( Expression  t) { return  t != null && t.getTag() == Formula.SETEXT ; }static private  Expression[]  tom_get_slot_SetExtension_members( Expression  t) { return  ((SetExtension)t).getMembers() ; }    /*   * UnaryExpression   */     static private boolean tom_is_fun_sym_UnaryExpression( Expression  t) { return  t instanceof UnaryExpression ; }static private  Expression  tom_get_slot_UnaryExpression_child( Expression  t) { return  ((UnaryExpression) t).getChild() ; }static private boolean tom_is_fun_sym_Card( Expression  t) { return  t != null && t.getTag() == Formula.KCARD ; }static private  Expression  tom_get_slot_Card_child( Expression  t) { return  ((UnaryExpression) t).getChild() ; }static private boolean tom_is_fun_sym_Pow( Expression  t) { return  t != null && t.getTag() == Formula.POW ; }static private  Expression  tom_get_slot_Pow_child( Expression  t) { return  ((UnaryExpression) t).getChild() ; }static private boolean tom_is_fun_sym_Pow1( Expression  t) { return  t != null && t.getTag() == Formula.POW1 ; }static private  Expression  tom_get_slot_Pow1_child( Expression  t) { return  ((UnaryExpression) t).getChild() ; }static private boolean tom_is_fun_sym_Union( Expression  t) { return  t != null && t.getTag() == Formula.KUNION ; }static private  Expression  tom_get_slot_Union_child( Expression  t) { return  ((UnaryExpression) t).getChild() ; }static private boolean tom_is_fun_sym_Inter( Expression  t) { return  t != null && t.getTag() == Formula.KINTER ; }static private  Expression  tom_get_slot_Inter_child( Expression  t) { return  ((UnaryExpression) t).getChild() ; }static private boolean tom_is_fun_sym_Dom( Expression  t) { return  t != null && t.getTag() == Formula.KDOM ; }static private  Expression  tom_get_slot_Dom_child( Expression  t) { return  ((UnaryExpression) t).getChild() ; }static private boolean tom_is_fun_sym_Ran( Expression  t) { return  t != null && t.getTag() == Formula.KRAN ; }static private  Expression  tom_get_slot_Ran_child( Expression  t) { return  ((UnaryExpression) t).getChild() ; }static private boolean tom_is_fun_sym_Prj1( Expression  t) { return  t != null && t.getTag() == Formula.KPRJ1 ; }static private  Expression  tom_get_slot_Prj1_child( Expression  t) { return  ((UnaryExpression) t).getChild() ; }static private boolean tom_is_fun_sym_Prj2( Expression  t) { return  t != null && t.getTag() == Formula.KPRJ2 ; }static private  Expression  tom_get_slot_Prj2_child( Expression  t) { return  ((UnaryExpression) t).getChild() ; }static private boolean tom_is_fun_sym_Id( Expression  t) { return  t != null && t.getTag() == Formula.KID ; }static private  Expression  tom_get_slot_Id_child( Expression  t) { return  ((UnaryExpression) t).getChild() ; }static private boolean tom_is_fun_sym_Min( Expression  t) { return  t != null && t.getTag() == Formula.KMIN ; }static private  Expression  tom_get_slot_Min_child( Expression  t) { return  ((UnaryExpression) t).getChild() ; }static private boolean tom_is_fun_sym_Max( Expression  t) { return  t != null && t.getTag() == Formula.KMAX ; }static private  Expression  tom_get_slot_Max_child( Expression  t) { return  ((UnaryExpression) t).getChild() ; }static private boolean tom_is_fun_sym_Converse( Expression  t) { return  t != null && t.getTag() == Formula.CONVERSE ; }static private  Expression  tom_get_slot_Converse_child( Expression  t) { return  ((UnaryExpression) t).getChild() ; }static private boolean tom_is_fun_sym_UnMinus( Expression  t) { return  t != null && t.getTag() == Formula.UNMINUS ; }static private  Expression  tom_get_slot_UnMinus_child( Expression  t) { return  ((UnaryExpression) t).getChild() ; }      /*TODO: Delete these...*/    static private boolean tom_is_fun_sym_PowSet( Type  t) { return  t instanceof PowerSetType ; }static private  Type  tom_get_slot_PowSet_child( Type  t) { return  ((PowerSetType) t).getBaseType() ; }static private boolean tom_is_fun_sym_CProd( Type  t) { return  t instanceof ProductType ; }static private  Type  tom_get_slot_CProd_left( Type  t) { return  ((ProductType) t).getLeft() ; }static private  Type  tom_get_slot_CProd_right( Type  t) { return  ((ProductType) t).getRight() ; }static private boolean tom_is_fun_sym_Set( Type  t) { return  t instanceof GivenType ; }static private String tom_get_slot_Set_name( Type  t) { return  ((GivenType) t).getName() ; }  

	private final Counter c = new Counter();
	private final LinkedList<BoundIdentDecl> identDecls = new LinkedList<BoundIdentDecl>();
	private boolean hasPushed = false;
	protected final FormulaFactory ff;

	public DecomposedQuant(FormulaFactory ff) {
		this.ff = ff;
	}
	
	public Expression addQuantifier(Type type, SourceLocation loc) {
		return addQuantifier(type, "x", loc);
	}	

	public Expression addQuantifier(Type type, String name, SourceLocation loc) {
		assert !hasPushed : "Tried to add quantifiers after having started pushing stuff";
		List<BoundIdentDecl> newIdentDecls = new LinkedList<BoundIdentDecl>();
		Expression result = mapletOfType(newIdentDecls, type, name, loc);
		identDecls.addAll(0, newIdentDecls);
		return result;
	}
	
	public Expression push(Expression expr) {
		hasPushed = true;
		return expr.shiftBoundIdentifiers(offset(), ff); 
	}
	
	public static Expression pushThroughAll(
		Expression expr, FormulaFactory ff, DecomposedQuant... quantifications) {
		int totalOffset = 0;
		for(DecomposedQuant quantification: quantifications) {
			quantification.hasPushed = true;
			totalOffset += quantification.offset();
		}
		return expr.shiftBoundIdentifiers(totalOffset, ff);
	}
	
	public Expression makeQuantifiedExpression(
		int tag, Predicate pred, Expression expr, SourceLocation loc) {

		return ff.makeQuantifiedExpression(
			tag, identDecls, pred, expr, loc, QuantifiedExpression.Form.Explicit);
	}
		
	public Predicate makeQuantifiedPredicate(
		int tag, Predicate pred, SourceLocation loc) {

		return ff.makeQuantifiedPredicate(
			tag, identDecls, pred, loc);
	}

	protected List<BoundIdentDecl> getIdentDecls() {
		return identDecls;
	}
	
	protected List<BoundIdentDecl> X() {
		return identDecls;
	}

	public int offset() {
		return identDecls.size();
	}
	
	private Expression mapletOfType(List<BoundIdentDecl> newIdentDecls, 
		Type type, String name, SourceLocation loc) {
		    { Type  tom_match1_1=(( Type )type);
    if(tom_is_fun_sym_CProd(tom_match1_1)) {
      { Type  tom_match1_1_left=tom_get_slot_CProd_left(tom_match1_1);
      { Type  tom_match1_1_right=tom_get_slot_CProd_right(tom_match1_1);

				Expression r = mapletOfType(newIdentDecls, tom_match1_1_right, name, loc);
				Expression l = mapletOfType(newIdentDecls, tom_match1_1_left, name, loc);
				
				return ff.makeBinaryExpression(Formula.MAPSTO, l, r, loc);
			}
}
    }

				newIdentDecls.add(0, ff.makeBoundIdentDecl(name, loc, type));
				return ff.makeBoundIdentifier(c.increment(), loc, type);	
			}

	}
}