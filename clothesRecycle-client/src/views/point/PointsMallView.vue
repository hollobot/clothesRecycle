<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { exchangeGift, getGiftList, getMyGiftExchanges } from '@/api/gift'

const gifts = ref([])
const exchanges = ref([])
const loading = ref(false)

const latestExchanges = computed(() => exchanges.value.slice(0, 8))

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
        <img v-if="gift.imageUrl" :src="gift.imageUrl" class="gift-cover-img" alt="gift" />
        <div v-else class="gift-cover">礼品</div>
        <div class="gift-main">
          <p class="gift-name">{{ gift.name }}</p>
          <small class="gift-desc">{{ gift.description || '暂无描述' }}</small>
          <small class="gift-meta">所需积分：{{ gift.pointCost }}，库存：{{ gift.stock }}</small>
        </div>
        <el-button type="primary" :disabled="gift.stock <= 0" @click="doExchange(gift.id)">
          {{ gift.stock > 0 ? '兑换' : '缺货' }}
        </el-button>
      </article>
    </div>

    <div class="section" v-loading="loading">
      <div class="section-title-row">
        <h3>我的兑换记录</h3>
      </div>

      <el-empty v-if="latestExchanges.length === 0" description="暂无兑换记录" />

      <article class="point-record" v-for="record in latestExchanges" :key="record.id">
        <div>
          <p class="point-record-title">兑换码：{{ record.exchangeCode }}</p>
          <small class="point-record-time">{{
            record.createTime?.slice(0, 16)?.replace('T', ' ') || '-'
          }}</small>
        </div>
        <el-tag :type="record.status === 'VERIFIED' ? 'info' : 'success'" effect="light">
          {{ record.status === 'VERIFIED' ? '已核销' : '待核销' }}
        </el-tag>
      </article>
    </div>
  </div>
</template>
