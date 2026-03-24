<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { exchangeGift, getGiftList, getMyGiftExchanges } from '@/api/gift'

const gifts = ref([])
const exchanges = ref([])
const loading = ref(false)

const latestExchanges = computed(() => exchanges.value.slice(0, 8))

/**
 * 礼品索引，便于在兑换记录中回填礼品名称与图片。
 */
const giftMap = computed(() => {
  const map = new Map()
  gifts.value.forEach((gift) => {
    map.set(gift.id, gift)
  })
  return map
})

/**
 * 展示用图片地址标准化。
 *
 * @param {string} url 图片地址
 */
const normalizeImageUrl = (url) => {
  const value = String(url || '').trim()
  return value || ''
}

/**
 * 礼品库存状态文案。
 *
 * @param {{stock:number}} gift 礼品对象
 */
const stockLabel = (gift) => (Number(gift.stock || 0) > 0 ? '可兑换' : '已缺货')

/**
 * 兑换记录状态文案。
 *
 * @param {string} status 兑换状态
 */
const exchangeStatusLabel = (status) => (status === 'VERIFIED' ? '已核销' : '待核销')

const load = async () => {
  loading.value = true
  try {
    const [giftList, exchangeList] = await Promise.all([getGiftList(), getMyGiftExchanges()])
    gifts.value = Array.isArray(giftList) ? giftList : []
    exchanges.value = Array.isArray(exchangeList) ? exchangeList : []
  } finally {
    loading.value = false
  }
}

// 兑换前二次确认，避免误扣积分。
const doExchange = async (giftId) => {
  try {
    await ElMessageBox.confirm('确认兑换该礼品吗？', '兑换确认', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning',
    })

    const data = await exchangeGift(giftId)
    ElMessage.success(`兑换成功，兑换码：${data.exchangeCode}`)
    await load()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '兑换失败')
    }
  }
}

onMounted(async () => {
  try {
    await load()
  } catch (error) {
    ElMessage.error(error.message || '加载礼品商城失败')
  }
})
</script>

<template>
  <div class="page">
    <div class="topbar">
      <h1>积分礼品商城</h1>
      <button class="ghost-btn" @click="$router.push('/points')">返回积分中心</button>
    </div>

    <div class="section" v-loading="loading">
      <div class="section-title-row">
        <h3>可兑换礼品</h3>
      </div>

      <el-empty v-if="gifts.length === 0" description="暂无可兑换礼品" />

      <article class="gift-row" v-for="gift in gifts" :key="gift.id">
        <img
          v-if="normalizeImageUrl(gift.imageUrl)"
          :src="normalizeImageUrl(gift.imageUrl)"
          class="gift-cover-img"
          :alt="gift.name || 'gift'"
        />
        <div v-else class="gift-cover">礼品</div>
        <div class="gift-main">
          <p class="gift-name">{{ gift.name }}</p>
          <small class="gift-desc">{{ gift.description || '暂无描述' }}</small>
          <div class="gift-meta-wrap">
            <small class="gift-meta">所需积分：{{ gift.pointCost }}</small>
            <small class="gift-meta">库存：{{ gift.stock }}</small>
            <small class="gift-meta">已兑换：{{ gift.exchangedCount || 0 }}</small>
          </div>
        </div>
        <div class="gift-action">
          <el-tag :type="gift.stock > 0 ? 'success' : 'info'" effect="plain">
            {{ stockLabel(gift) }}
          </el-tag>
          <el-button type="primary" :disabled="gift.stock <= 0" @click="doExchange(gift.id)">
            {{ gift.stock > 0 ? '兑换' : '缺货' }}
          </el-button>
        </div>
      </article>
    </div>

    <div class="section" v-loading="loading">
      <div class="section-title-row">
        <h3>我的兑换记录</h3>
      </div>

      <el-empty v-if="latestExchanges.length === 0" description="暂无兑换记录" />

      <article class="point-record" v-for="record in latestExchanges" :key="record.id">
        <div class="record-cover-wrap">
          <img
            v-if="normalizeImageUrl(giftMap.get(record.giftId)?.imageUrl)"
            :src="normalizeImageUrl(giftMap.get(record.giftId)?.imageUrl)"
            class="record-cover"
            alt="gift"
          />
          <div v-else class="record-cover empty">礼品</div>
        </div>
        <div>
          <p class="point-record-gift">
            {{ giftMap.get(record.giftId)?.name || `礼品 #${record.giftId}` }}
          </p>
          <p class="point-record-title">兑换码：{{ record.exchangeCode }}</p>
          <small class="point-record-time">{{
            record.createTime?.slice(0, 16)?.replace('T', ' ') || '-'
          }}</small>
        </div>
        <el-tag :type="record.status === 'VERIFIED' ? 'info' : 'success'" effect="light">
          {{ exchangeStatusLabel(record.status) }}
        </el-tag>
      </article>
    </div>
  </div>
</template>

<style scoped>
.gift-row {
  display: grid;
  grid-template-columns: 92px 1fr auto;
  gap: 14px;
  align-items: center;
  padding: 14px 0;
  border-bottom: 1px solid #edf0f2;
}

.gift-cover-img,
.gift-cover {
  width: 92px;
  height: 92px;
  border-radius: 10px;
  object-fit: cover;
}

.gift-cover {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #8a8a8a;
  font-size: 13px;
  border: 1px dashed #d6dde5;
  background: #f7f9fb;
}

.gift-main {
  min-width: 0;
}

.gift-name {
  margin: 0 0 4px;
  font-weight: 700;
}

.gift-desc {
  display: block;
  margin-bottom: 8px;
  color: #5f6b76;
}

.gift-meta-wrap {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.gift-meta {
  color: #6f7b86;
}

.gift-action {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 8px;
}

.point-record {
  display: grid;
  grid-template-columns: 56px 1fr auto;
  gap: 12px;
  align-items: center;
}

.record-cover-wrap {
  width: 56px;
  height: 56px;
}

.record-cover {
  width: 56px;
  height: 56px;
  border-radius: 8px;
  object-fit: cover;
}

.record-cover.empty {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #8a8a8a;
  font-size: 12px;
  border: 1px dashed #d6dde5;
  background: #f7f9fb;
}

.point-record-gift {
  margin: 0 0 4px;
  font-weight: 600;
}

.point-record-title {
  margin: 0;
}
</style>
