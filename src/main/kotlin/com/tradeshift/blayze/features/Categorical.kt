package com.tradeshift.blayze.features

import com.tradeshift.blayze.Protos
import com.tradeshift.blayze.collection.Counter
import com.tradeshift.blayze.dto.FeatureValue
import com.tradeshift.blayze.dto.Outcome

/**
 * A feature for categorical data, i.e. 1 of K data, e.g. user-ids, countries, etc.
 */
class Categorical(private val delegate: Multinomial = Multinomial()) : Feature<Categorical, FeatureValue> {

    fun toProto(): Protos.Categorical {
        return Protos.Categorical.newBuilder().setDelegate(delegate.toProto()).build()
    }

    companion object {
        fun fromProto(proto: Protos.Categorical): Categorical {
            return Categorical(Multinomial.fromProto(proto.delegate))
        }
    }

    override fun batchUpdate(updates: List<Pair<Outcome, FeatureValue>>): Categorical {
        val categories = updates.map { Pair(it.first, Counter(it.second)) }
        return Categorical(delegate.batchUpdate(categories))
    }

    override fun logProbability(outcomes: Set<Outcome>, value: FeatureValue): Map<Outcome, Double> {
        val counts = Counter(value)
        return delegate.logProbability(outcomes, counts)
    }
}
