import {
    ASK_GPT_URL,
    SUMMARIZE,
    CORRECT_GPT_URL,
} from './url'
import { Get, Post, Delete, Download } from "@/api/request";
export default {
    askGpt: (params) => {
        return Post(ASK_GPT_URL, params);
    },
    summarize: (params) => {
        return Post(SUMMARIZE, params); // 传入的参数是一个对象
    },
    correctGpt: (params) => {
        return Post(CORRECT_GPT_URL, params); // 传入的参数是一个对象
    }
}